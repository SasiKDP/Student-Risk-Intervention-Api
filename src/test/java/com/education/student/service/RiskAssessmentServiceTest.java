package com.education.student.service;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.AtRiskStudentDto;
import com.education.student.dto.StudentRiskAssessmentDto;
import com.education.student.exceptions.ResourceNotFoundException;
import com.education.student.model.*;
import com.education.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class RiskAssessmentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private RiskAssessmentService riskAssessmentService;

    private Students mockStudent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockStudent = new Students();
        mockStudent.setId(UUID.randomUUID());
        mockStudent.setName("Test Student");

        AcademicPerformance academic = new AcademicPerformance();
        academic.setSemester("SEM-1");
        academic.setGrade(BigDecimal.valueOf(65));
        academic.setStateAssessmentELA(480);
        academic.setStateAssessmentMath(510);

        Attendance attendance = new Attendance();
        attendance.setSemester("SEM-1");
        attendance.setAttendanceRate(BigDecimal.valueOf(85));
        attendance.setAbsentDays(12);
        attendance.setTardyDays(6);

        Behavior behavior = new Behavior();
        behavior.setSemester("SEM-1");
        behavior.setDisciplinaryActions(3);
        behavior.setSuspensions(1);

        mockStudent.setAcademicRecords(List.of(academic));
        mockStudent.setAttendanceRecords(List.of(attendance));
        mockStudent.setBehaviorRecords(List.of(behavior));
    }

    @Test
    void testCalculateRiskScore_HighRisk() {
        when(studentRepository.findById(any())).thenReturn(Optional.of(mockStudent));
        ResponseEntity<ApiResponse<StudentRiskAssessmentDto>> response =
                riskAssessmentService.calculateRiskScore(mockStudent.getId().toString(), "SEM-1");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(RiskLevel.HIGH, response.getBody().getData().getRiskLevel());
    }

    @Test
    void testCalculateRiskScore_InvalidUUID() {
        ResponseEntity<ApiResponse<StudentRiskAssessmentDto>> response =
                riskAssessmentService.calculateRiskScore("invalid-uuid", "SEM-1");

        assertTrue(response.getStatusCode().is4xxClientError());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_UUID", response.getBody().getError().getErrorCode());
    }

    @Test
    void testCalculateRiskScore_StudentNotFound() {
        when(studentRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                riskAssessmentService.calculateRiskScore(UUID.randomUUID().toString(), "SEM-1"));
    }

    @Test
    void testIdentifyAtRiskStudents_ReturnsPage() {
        when(studentRepository.findAll()).thenReturn(List.of(mockStudent));
        when(studentRepository.findById(mockStudent.getId())).thenReturn(Optional.of(mockStudent));

        Pageable pageable = PageRequest.of(0, 10);
        ResponseEntity<ApiResponse<Page<AtRiskStudentDto>>> response =
                riskAssessmentService.identifyAtRiskStudents("SEM-1", RiskLevel.MEDIUM, pageable);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getTotalElements());
        assertEquals(RiskLevel.HIGH, response.getBody().getData().getContent().get(0).getRiskLevel());
    }
}
