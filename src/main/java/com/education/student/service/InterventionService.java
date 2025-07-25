package com.education.student.service;

import com.education.student.dto.*;
import com.education.student.exceptions.ResourceNotFoundException;
import com.education.student.model.Intervention;
import com.education.student.model.Intervention.InterventionStatus;
import com.education.student.model.Students;
import com.education.student.repository.InterventionRepository;
import com.education.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InterventionService {

    @Autowired
    InterventionRepository interventionRepository;
    @Autowired
    StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(InterventionService.class);

    public ResponseEntity<ApiResponse<Intervention>> createIntervention(CreateInterventionRequest request) {
        logger.info("Creating intervention for student ID: {}", request.getStudentId());

        Students student = studentRepository.findById(UUID.fromString(request.getStudentId()))
                    .orElseThrow(() -> {
                    logger.warn("Student not found with ID: {}", request.getStudentId());
                    return new ResourceNotFoundException("Student not found with ID: " + request.getStudentId());
                });

        List<InterventionRecommendation> recommendations = recommendInterventions(request.getStudentId()).getBody().getData();
        String recommendedType = recommendations.isEmpty() ? "General Mentoring" : recommendations.get(0).getRecommendedType();

        Intervention intervention = new Intervention();
        intervention.setStudent(student);
        intervention.setInterventionType(recommendedType);
        intervention.setStartDate(request.getStartDate());
        intervention.setTargetCompletionDate(request.getTargetCompletionDate());
        intervention.setStartScore(request.getStartScore());
        intervention.setCurrentScore(request.getStartScore());
        intervention.setGoalScore(request.getGoalScore());
        intervention.setStatus(InterventionStatus.NOT_ON_TRACK);

        Intervention saved = interventionRepository.save(intervention);
        logger.info("Intervention created with ID: {}", saved.getId());
        return ResponseEntity.ok(ApiResponse.success("Intervention created", saved));
    }

    public ResponseEntity<ApiResponse<Intervention>> updateInterventionProgress(String interventionId, InterventionProgressUpdate update) {
        logger.info("Updating progress for intervention ID: {}", interventionId);
        UUID uuid = parseUUID(interventionId, "Invalid intervention ID format");

        Intervention intervention = interventionRepository.findById(uuid)
                .orElseThrow(() -> {
                    logger.warn("Intervention not found with ID: {}", interventionId);
                    return new ResourceNotFoundException("Intervention not found with ID: " + interventionId);
                });

        if (update.getCurrentScore() == null) {
            logger.warn("Rejected progress update — currentScore is null");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Current score must not be null", "INVALID_REQUEST", "currentScore cannot be null"));
        }

        intervention.setCurrentScore(update.getCurrentScore());

        if (update.getCurrentScore().compareTo(intervention.getGoalScore()) >= 0) {
            intervention.setStatus(InterventionStatus.COMPLETED);
        } else {
            boolean onTrack = isOnTrack(intervention);
            intervention.setStatus(onTrack ? InterventionStatus.ON_TRACK : InterventionStatus.NOT_ON_TRACK);
        }

        Intervention saved = interventionRepository.save(intervention);
        logger.info("Progress updated. New status: {}", saved.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Intervention progress updated", saved));
    }

    public ResponseEntity<ApiResponse<List<Intervention>>> getAllInterventions() {
        logger.info("Fetching all interventions from the database");
        List<Intervention> interventions = interventionRepository.findAll();
        logger.info("Total interventions fetched: {}", interventions.size());
        return ResponseEntity.ok(ApiResponse.success("All interventions retrieved", interventions));
    }

    public ResponseEntity<ApiResponse<List<Intervention>>> getStudentInterventions(String studentId) {
        logger.info("Fetching interventions for student ID: {}", studentId);
        UUID uuid = parseUUID(studentId, "Invalid student ID format");

        Students student = studentRepository.findById(uuid)
                .orElseThrow(() -> {
                    logger.warn("Student not found for intervention query: {}", studentId);
                    return new ResourceNotFoundException("Student not found with ID: " + studentId);
                });

        List<Intervention> results = interventionRepository.findByStudent(student);
        logger.info("Found {} interventions for student: {}", results.size(), student.getName());
        return ResponseEntity.ok(ApiResponse.success("Interventions retrieved", results));
    }

    public ResponseEntity<ApiResponse<Boolean>> isStudentOnTrack(String interventionId) {
        logger.info("Evaluating on-track status for intervention ID: {}", interventionId);
        UUID uuid = parseUUID(interventionId, "Invalid intervention ID format");

        Intervention intervention = interventionRepository.findById(uuid)
                .orElseThrow(() -> {
                    logger.warn("Intervention not found: {}", interventionId);
                    return new ResourceNotFoundException("Intervention not found with ID: " + interventionId);
                });

        boolean onTrack = isOnTrack(intervention);
        logger.info("Intervention ID {} is {} track", interventionId, onTrack ? "on" : "not on");
        return ResponseEntity.ok(ApiResponse.success("On-track status evaluated", onTrack));
    }

    public ResponseEntity<ApiResponse<List<InterventionRecommendation>>> recommendInterventions(String studentId) {
        logger.info("Calculating intervention recommendation for student ID: {}", studentId);

        Students student = studentRepository.findById(UUID.fromString(studentId))
                .orElseThrow(() -> {
                    logger.warn("Student not found for recommendations: {}", studentId);
                    return new ResourceNotFoundException("Student not found with ID: " + studentId);
                });

        String studentName = student.getName();

        boolean academicRisk = student.getAcademicRecords().stream()
                .anyMatch(a -> a.getGrade().compareTo(BigDecimal.valueOf(70)) < 0 ||
                        a.getStateAssessmentELA() < 500 ||
                        a.getStateAssessmentMath() < 500);

        boolean attendanceRisk = student.getAttendanceRecords().stream()
                .anyMatch(a -> a.getAttendanceRate().compareTo(BigDecimal.valueOf(90)) < 0 ||
                        a.getAbsentDays() > 10 ||
                        a.getTardyDays() > 5);

        boolean behaviorRisk = student.getBehaviorRecords().stream()
                .anyMatch(b -> b.getDisciplinaryActions() > 2 ||
                        b.getSuspensions() > 0);

        List<InterventionRecommendation> recommendations = new ArrayList<>();
        if (academicRisk) {
            recommendations.add(new InterventionRecommendation(studentName, "Tutoring", "Student shows signs of academic risk."));
        } else if (attendanceRisk) {
            recommendations.add(new InterventionRecommendation(studentName, "Attendance Counseling", "Low attendance or excessive absences/tardies."));
        } else if (behaviorRisk) {
            recommendations.add(new InterventionRecommendation(studentName, "Behavior Support Plan", "Multiple disciplinary actions or suspensions."));
        } else {
            recommendations.add(new InterventionRecommendation(studentName, "General Mentoring", "No specific risk identified."));
        }

        logger.debug("Generated {} recommendations for student: {}", recommendations.size(), studentName);
        return ResponseEntity.ok(ApiResponse.success("Recommendations generated", recommendations));
    }

    private boolean isOnTrack(Intervention i) {
        if (i.getStatus() == InterventionStatus.COMPLETED) return true;

        long totalDays = ChronoUnit.DAYS.between(i.getStartDate(), i.getTargetCompletionDate());
        long daysPassed = ChronoUnit.DAYS.between(i.getStartDate(), LocalDate.now());

        if (totalDays <= 0 || daysPassed < 0) return false;

        BigDecimal totalImprovement = i.getGoalScore().subtract(i.getStartScore());
        if (totalImprovement.compareTo(BigDecimal.ZERO) <= 0) return false;

        BigDecimal expectedProgressRatio = BigDecimal.valueOf((double) daysPassed / totalDays);
        BigDecimal expectedScore = i.getStartScore().add(totalImprovement.multiply(expectedProgressRatio));

        logger.debug("Progress for intervention ID {} — Expected: {}, Actual: {}", i.getId(), expectedScore, i.getCurrentScore());
        return i.getCurrentScore().compareTo(expectedScore) >= 0;
    }

    private UUID parseUUID(String id, String errorMessage) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            logger.warn("UUID parsing failed: {} — {}", id, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
