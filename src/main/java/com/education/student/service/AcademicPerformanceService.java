package com.education.student.service;

import com.education.student.dto.AcademicPerformanceDTO;
import com.education.student.dto.ApiResponse;
import com.education.student.model.AcademicPerformance;
import com.education.student.model.Students;
import com.education.student.repository.AcademicPerformanceRepo;
import com.education.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class AcademicPerformanceService {

    private static final Logger logger = LoggerFactory.getLogger(AcademicPerformanceService.class);

    private final AcademicPerformanceRepo academicPerformanceRepo;
    private final StudentRepository studentRepo;
    @Autowired
    public AcademicPerformanceService(AcademicPerformanceRepo academicPerformanceRepo,
                                      StudentRepository studentRepo) {
        this.academicPerformanceRepo = academicPerformanceRepo;
        this.studentRepo = studentRepo;
    }

    public ResponseEntity<ApiResponse<Object>> addAcademicPerformance(AcademicPerformanceDTO dto) {
        logger.info("Adding academic performance for student ID: {}", dto.getStudentId());

        if (dto.getStudentId() == null) {
            logger.warn("Rejected: Student ID is null");
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Student ID must not be null.", "BAD_REQUEST", null)
            );
        }

        try {
            Students student = studentRepo.findById(dto.getStudentId())
                    .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + dto.getStudentId()));

            if (dto.getCourse() == null || dto.getSemester() == null || dto.getGrade() == null) {
                logger.warn("Missing course, semester, or grade");
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Course, semester, and grade must not be null.", "BAD_REQUEST", null)
                );
            }

            AcademicPerformance entity = convertToEntity(dto, student);
            academicPerformanceRepo.save(entity);

            logger.info("Saved academic record: studentId={}, course={}, semester={}",
                    student.getId(), dto.getCourse(), dto.getSemester());

            List<AcademicPerformance> records = academicPerformanceRepo.findByStudentId(dto.getStudentId());
            Map<String, Object> gradeSummary = evaluateStudentGrades(records);
            String overallGrade = (String) gradeSummary.get("overallGrade");

            logger.info("Student {} re-evaluated overall grade: {}", student.getName(), overallGrade);

            return ResponseEntity.ok(ApiResponse.success(
                    "Academic record saved. Grade updated to '" + overallGrade + "'.", gradeSummary));

        } catch (NoSuchElementException e) {
            logger.warn("Student not found: {}", dto.getStudentId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(e.getMessage(), "STUDENT_NOT_FOUND", null));
        } catch (Exception e) {
            logger.error("Unexpected error saving academic record: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Unexpected error occurred.", "INTERNAL_ERROR", e.getMessage()));
        }
    }

    public List<AcademicPerformance> getALlPerformanceList() {
        logger.info("Fetching all academic performance records");
        return academicPerformanceRepo.findAll();
    }

    public AcademicPerformance convertToEntity(AcademicPerformanceDTO dto, Students student) {
        AcademicPerformance entity = new AcademicPerformance();
        entity.setStudent(student);
        entity.setSemester(dto.getSemester());
        entity.setCourse(dto.getCourse());
        entity.setGrade(dto.getGrade());
        entity.setStateAssessmentELA(dto.getStateAssessmentELA());
        entity.setStateAssessmentMath(dto.getStateAssessmentMath());
        return entity;
    }

    public Map<String, Object> evaluateStudentGrades(List<AcademicPerformance> records) {
        Map<String, List<BigDecimal>> semesterGrades = new HashMap<>();
        List<BigDecimal> allGrades = new ArrayList<>();

        for (AcademicPerformance record : records) {
            if (record.getGrade() != null) {
                semesterGrades.computeIfAbsent(record.getSemester(), k -> new ArrayList<>()).add(record.getGrade());
                allGrades.add(record.getGrade());
            }
        }

        Map<String, String> semesterGradeMap = new HashMap<>();
        for (Map.Entry<String, List<BigDecimal>> entry : semesterGrades.entrySet()) {
            double avg = average(entry.getValue());
            String grade = gradeLetter(avg * 10);
            semesterGradeMap.put(entry.getKey(), grade);
            logger.info("Semester '{}' → Avg: {}, Grade: {}", entry.getKey(), avg * 10, grade);
        }

        double overallAvg = average(allGrades);
        String overallGrade = gradeLetter(overallAvg * 10);
        logger.info("Overall average: {}, Grade: {}", overallAvg * 10, overallGrade);

        if (!records.isEmpty()) {
            UUID studentId = records.get(0).getStudent().getId();
            studentRepo.findById(studentId).ifPresent(student -> {
                student.setGrade(overallGrade);
                studentRepo.save(student);
                logger.info("Updated grade '{}' for student '{}'", overallGrade, student.getName());
            });
        }

        Map<String, Object> result = new HashMap<>();
        result.put("semesterGrades", semesterGradeMap);
        result.put("overallAverage", overallAvg * 10);
        result.put("overallGrade", overallGrade);
        return result;
    }

    private double average(List<BigDecimal> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;
        BigDecimal sum = grades.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(grades.size()), 2, RoundingMode.HALF_UP).doubleValue();
    }

    private String gradeLetter(double percentage) {
        if (percentage >= 70) return "A";
        else if (percentage >= 40) return "B";
        else return "C";
    }
}
