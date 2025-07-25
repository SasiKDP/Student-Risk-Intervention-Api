package com.education.student.controller;

import com.education.student.dto.*;
import com.education.student.exceptions.ResourceNotFoundException;
import com.education.student.model.RiskLevel;
import com.education.student.service.RiskAssessmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class RiskAssessmentController {

    private static final Logger logger = LoggerFactory.getLogger(RiskAssessmentController.class);
    @Autowired
    RiskAssessmentService riskAssessmentService;

    @GetMapping("/risk/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<StudentRiskAssessmentDto>> getStudentRiskAssessment(
            @PathVariable String studentId,
            @RequestParam(required = false) String semester) {

        logger.info("Student is at Risk in semester={}", studentId, semester);

        try {
            StudentRiskAssessmentDto assessment = riskAssessmentService
                    .calculateRiskScore(studentId, semester)
                    .getBody()
                    .getData();

            logger.info("Risk calculated: studentId={}, score={}, level={}",
                    studentId, assessment.getRiskScore(), assessment.getRiskLevel());

            return ResponseEntity.ok(ApiResponse.success("Student risk assessment retrieved", assessment));
        } catch (ResourceNotFoundException e) {
            logger.warn("Student not found: ID={}", studentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Student not found", "STUDENT_NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error during risk assessment for studentId={}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Internal server error", "INTERNAL_ERROR", e.getMessage()));
        }
    }

    @GetMapping("/at-risk")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<AtRiskStudentDto>>> getAtRiskStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) RiskLevel minimumRisk) {

        logger.info("GET /student/at-risk - page={}, size={}, semester={}, minRisk={}",
                page, size, semester, minimumRisk);

        try {
            Page<AtRiskStudentDto> result = riskAssessmentService
                    .identifyAtRiskStudents(semester, minimumRisk, PageRequest.of(page, size))
                    .getBody()
                    .getData();

            logger.info("At-risk students retrieved. Count={}", result.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success("At-risk students retrieved", result));
        } catch (Exception e) {
            logger.error("Failed to retrieve at-risk students: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error retrieving at-risk students", "INTERNAL_ERROR", e.getMessage()));
        }
    }
}
