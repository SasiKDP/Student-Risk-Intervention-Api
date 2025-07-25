package com.education.student.service;

import com.education.student.dto.*;
import com.education.student.exceptions.ResourceNotFoundException;
import com.education.student.model.RiskLevel;
import com.education.student.model.Students;
import com.education.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private static final Logger logger = LoggerFactory.getLogger(RiskAssessmentService.class);
    @Autowired
    StudentRepository studentRepository;

    public ResponseEntity<ApiResponse<StudentRiskAssessmentDto>> calculateRiskScore(String studentId, String semester) {
        logger.info("Calculating risk score for studentId={} semester={}", studentId, semester);

        try {
            Students student = studentRepository.findById(UUID.fromString(studentId))
                    .orElseThrow(() -> {
                        logger.warn("Student not found with ID: {}", studentId);
                        return new ResourceNotFoundException("Student not found with ID: " + studentId);
                    });

            AtomicInteger score = new AtomicInteger(0);

            OptionalDouble avgGrade = student.getAcademicRecords().stream()
                    .filter(a -> semester == null || semester.equals(a.getSemester()))
                    .mapToDouble(a -> a.getGrade().doubleValue())
                    .average();

            if (avgGrade.isPresent()) {
                logger.debug("Average grade: {}", avgGrade.getAsDouble());
                if (avgGrade.getAsDouble() < 70) {
                    score.addAndGet(25);
                    logger.debug("Added 25 points for low GPA");
                }
            }

            boolean lowAssessments = student.getAcademicRecords().stream()
                    .filter(a -> semester == null || semester.equals(a.getSemester()))
                    .anyMatch(a -> a.getStateAssessmentELA() < 500 || a.getStateAssessmentMath() < 500);

            if (lowAssessments) {
                score.addAndGet(15);
                logger.debug("Added 15 points for low state assessments");
            }

            student.getAttendanceRecords().stream()
                    .filter(a -> semester == null || semester.equals(a.getSemester()))
                    .findFirst().ifPresent(a -> {
                        if (a.getAttendanceRate().compareTo(BigDecimal.valueOf(90)) < 0) {
                            score.addAndGet(20);
                            logger.debug("Added 20 points for attendance rate < 90%");
                        }
                        if (a.getAbsentDays() > 10) {
                            score.addAndGet(10);
                            logger.debug("Added 10 points for > 10 absences");
                        }
                        if (a.getTardyDays() > 5) {
                            score.addAndGet(10);
                            logger.debug("Added 10 points for > 5 tardies");
                        }
                    });

            student.getBehaviorRecords().stream()
                    .filter(b -> semester == null || semester.equals(b.getSemester()))
                    .findFirst().ifPresent(b -> {
                        if (b.getDisciplinaryActions() > 2) {
                            score.addAndGet(15);
                            logger.debug("Added 15 points for disciplinary actions");
                        }
                        if (b.getSuspensions() > 0) {
                            score.addAndGet(5);
                            logger.debug("Added 5 points for suspensions");
                        }
                    });

            int finalScore = score.get();
            RiskLevel level = (finalScore >= 70) ? RiskLevel.HIGH :
                    (finalScore >= 40 ? RiskLevel.MEDIUM : RiskLevel.LOW);

            StudentRiskAssessmentDto result = new StudentRiskAssessmentDto(studentId, finalScore, level, semester);
            logger.info("Calculated risk score for student {}: {} ({})", studentId, finalScore, level);

            return ResponseEntity.ok(ApiResponse.success("Risk assessment calculated successfully", result));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid student UUID format: {}", studentId, e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Invalid student ID format", "INVALID_UUID", e.getMessage())
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while calculating risk score for studentId={}", studentId, e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("Error while calculating risk score", "INTERNAL_ERROR", e.getMessage())
            );
        }
    }

    public ResponseEntity<ApiResponse<Page<AtRiskStudentDto>>> identifyAtRiskStudents(String semester, RiskLevel minRisk, Pageable pageable) {
        RiskLevel effectiveRisk = (minRisk != null) ? minRisk : RiskLevel.MEDIUM;
        logger.info("Identifying students at risk for semester={} with minRisk={}", semester, effectiveRisk);

        try {
            List<AtRiskStudentDto> atRisk = studentRepository.findAll().stream()
                    .map(student -> {
                        try {
                            ResponseEntity<ApiResponse<StudentRiskAssessmentDto>> response = calculateRiskScore(student.getId().toString(), semester);
                            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().isSuccess()) {
                                StudentRiskAssessmentDto dto = response.getBody().getData();
                                return new AtRiskStudentDto(student.getId().toString(), student.getName(), dto.getRiskLevel(), dto.getRiskScore());
                            }
                        } catch (Exception ex) {
                            logger.warn("Skipping student {} due to error: {}", student.getId(), ex.getMessage());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .filter(dto -> dto.getRiskLevel().ordinal() >= effectiveRisk.ordinal())
                    .collect(Collectors.toList());

            logger.info("Identified {} at-risk students", atRisk.size());
            Page<AtRiskStudentDto> page = new PageImpl<>(atRisk, pageable, atRisk.size());

            return ResponseEntity.ok(ApiResponse.success("At-risk students retrieved", page));
        } catch (Exception e) {
            logger.error("Error identifying at-risk students", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("Failed to identify at-risk students", "INTERNAL_ERROR", e.getMessage())
            );
        }
    }
}