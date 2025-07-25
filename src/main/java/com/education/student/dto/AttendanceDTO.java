package com.education.student.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public class AttendanceDTO {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotBlank(message = "Semester is required")
    @Size(max = 20, message = "Semester must not exceed 20 characters")
    private String semester;

    @DecimalMin(value = "0.0", inclusive = true, message = "Attendance rate must be at least 0%")
    @DecimalMax(value = "100.0", inclusive = true, message = "Attendance rate must be at most 100%")
    private BigDecimal attendanceRate;

    @Min(value = 0, message = "Absent days cannot be negative")
    private int absentDays;

    @Min(value = 0, message = "Tardy days cannot be negative")
    private int tardyDays;

    public AttendanceDTO() {}

    public AttendanceDTO(UUID studentId, String semester, BigDecimal attendanceRate,
                         int absentDays, int tardyDays) {
        this.studentId = studentId;
        this.semester = semester;
        this.attendanceRate = attendanceRate;
        this.absentDays = absentDays;
        this.tardyDays = tardyDays;
    }

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public BigDecimal getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(BigDecimal attendanceRate) { this.attendanceRate = attendanceRate; }

    public int getAbsentDays() { return absentDays; }
    public void setAbsentDays(int absentDays) { this.absentDays = absentDays; }

    public int getTardyDays() { return tardyDays; }
    public void setTardyDays(int tardyDays) { this.tardyDays = tardyDays; }
}
