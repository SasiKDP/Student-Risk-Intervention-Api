package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.AtRiskStudentDto;
import com.education.student.dto.StudentRiskAssessmentDto;
import com.education.student.exceptions.ResourceNotFoundException;
import com.education.student.model.RiskLevel;
import com.education.student.service.RiskAssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RiskAssessmentControllerTest {

    @Mock
    RiskAssessmentService riskAssessmentService;

    @InjectMocks
    RiskAssessmentController riskAssessmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStudentRiskAssessment_Success() {
        StudentRiskAssessmentDto dto = new StudentRiskAssessmentDto("id", 80, RiskLevel.HIGH, "SEM-1");
        ApiResponse<StudentRiskAssessmentDto> apiResponse = ApiResponse.success("ok", dto);
        when(riskAssessmentService.calculateRiskScore("id", "SEM-1"))
                .thenReturn(ResponseEntity.ok(apiResponse));

        ResponseEntity<ApiResponse<StudentRiskAssessmentDto>> response =
                riskAssessmentController.getStudentRiskAssessment("id", "SEM-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(RiskLevel.HIGH, response.getBody().getData().getRiskLevel());
    }

    @Test
    void testGetStudentRiskAssessment_StudentNotFound() {
        when(riskAssessmentService.calculateRiskScore("id", "SEM-1"))
                .thenThrow(new ResourceNotFoundException("Student not found"));

        ResponseEntity<ApiResponse<StudentRiskAssessmentDto>> response =
                riskAssessmentController.getStudentRiskAssessment("id", "SEM-1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("STUDENT_NOT_FOUND", response.getBody().getError().getErrorCode());
    }

    @Test
    void testGetStudentRiskAssessment_InternalError() {
        when(riskAssessmentService.calculateRiskScore("id", "SEM-1"))
                .thenThrow(new RuntimeException("Unexpected"));

        ResponseEntity<ApiResponse<StudentRiskAssessmentDto>> response =
                riskAssessmentController.getStudentRiskAssessment("id", "SEM-1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INTERNAL_ERROR", response.getBody().getError().getErrorCode());
    }

    @Test
    void testGetAtRiskStudents_Success() {
        AtRiskStudentDto dto = new AtRiskStudentDto("id", "name", RiskLevel.HIGH, 80);
        Page<AtRiskStudentDto> page = new PageImpl<>(List.of(dto));
        ApiResponse<Page<AtRiskStudentDto>> apiResponse = ApiResponse.success("ok", page);

        when(riskAssessmentService.identifyAtRiskStudents("SEM-1", RiskLevel.MEDIUM, PageRequest.of(0, 20)))
                .thenReturn(ResponseEntity.ok(apiResponse));

        ResponseEntity<ApiResponse<Page<AtRiskStudentDto>>> response =
                riskAssessmentController.getAtRiskStudents(0, 20, "SEM-1", RiskLevel.MEDIUM);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getTotalElements());
    }

    @Test
    void testGetAtRiskStudents_InternalError() {
        when(riskAssessmentService.identifyAtRiskStudents("SEM-1", RiskLevel.MEDIUM, PageRequest.of(0, 20)))
                .thenThrow(new RuntimeException("Unexpected"));

        ResponseEntity<ApiResponse<Page<AtRiskStudentDto>>> response =
                riskAssessmentController.getAtRiskStudents(0, 20, "SEM-1", RiskLevel.MEDIUM);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INTERNAL_ERROR", response.getBody().getError().getErrorCode());
    }
}