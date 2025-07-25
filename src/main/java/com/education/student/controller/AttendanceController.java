package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.AttendanceDTO;
import com.education.student.service.AttendanceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
public class AttendanceController {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    AttendanceService attendanceService;

    @PostMapping("/createAttendance")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceDTO>> createAttendance(
            @Valid @RequestBody AttendanceDTO attendanceDTO) {

        ResponseEntity<ApiResponse<AttendanceDTO>> response = attendanceService.saveAttendance(attendanceDTO);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Attendance created for studentId={}", attendanceDTO.getStudentId());
        } else {
            logger.warn("Failed to create attendance for studentId={}: {}", attendanceDTO.getStudentId(),
                    response.getBody() != null ? response.getBody().getMessage() : "Unknown error");
        }

        return response;
    }
}
