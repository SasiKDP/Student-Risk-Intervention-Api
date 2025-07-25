package com.education.student.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private Students student;

    @NotBlank(message = "Semester is required")
    @Size(max = 20, message = "Semester must not exceed 20 characters")
    private String semester;

    @NotNull(message = "Attendance rate is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Attendance rate must be at least 0.00")
    @DecimalMax(value = "100.00", inclusive = true, message = "Attendance rate must not exceed 100.00")
    private BigDecimal attendanceRate;

    @Min(value = 0, message = "Absent days must be zero or positive")
    private int absentDays;

    @Min(value = 0, message = "Tardy days must be zero or positive")
    private int tardyDays;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Students getStudent() {
        return student;
    }

    public void setStudent(Students student) {
        this.student = student;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public BigDecimal getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(BigDecimal attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public int getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(int absentDays) {
        this.absentDays = absentDays;
    }

    public int getTardyDays() {
        return tardyDays;
    }

    public void setTardyDays(int tardyDays) {
        this.tardyDays = tardyDays;
    }
}
