package com.education.student.service;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.AttendanceDTO;
import com.education.student.model.Attendance;
import com.education.student.model.Students;
import com.education.student.repository.AttendanceRepository;
import com.education.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    public ResponseEntity<ApiResponse<AttendanceDTO>> saveAttendance(AttendanceDTO attendanceDTO) {
        try {
            Students student = studentRepository.findById(attendanceDTO.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + attendanceDTO.getStudentId()));

            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setSemester(attendanceDTO.getSemester());
            attendance.setAttendanceRate(attendanceDTO.getAttendanceRate());
            attendance.setAbsentDays(attendanceDTO.getAbsentDays());
            attendance.setTardyDays(attendanceDTO.getTardyDays());

            Attendance savedAttendance = attendanceRepository.save(attendance);
            AttendanceDTO dto = convertToDTO(savedAttendance);

            return ResponseEntity.ok(ApiResponse.success("Attendance record saved successfully.", dto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "STUDENT_NOT_FOUND", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Unexpected error occurred.", "INTERNAL_ERROR", e.getMessage()));
        }
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setStudentId(attendance.getStudent().getId());
        dto.setSemester(attendance.getSemester());
        dto.setAttendanceRate(attendance.getAttendanceRate());
        dto.setAbsentDays(attendance.getAbsentDays());
        dto.setTardyDays(attendance.getTardyDays());
        return dto;
    }
}
