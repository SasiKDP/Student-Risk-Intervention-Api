package com.education.student.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public class AcademicPerformanceDTO {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotBlank(message = "Semester is required")
    @Size(max = 20, message = "Semester must not exceed 20 characters")
    private String semester;

    @NotBlank(message = "Course is required")
    @Size(max = 100, message = "Course must not exceed 100 characters")
    private String course;

    @NotNull(message = "Grade is required")
    @DecimalMin(value = "0.00", message = "Grade must be at least 0.00")
    @DecimalMax(value = "100.00", message = "Grade must not exceed 100.00")
    private BigDecimal grade;

    @Min(value = 0, message = "ELA score must be non-negative")
    @Max(value = 100, message = "ELA score must not exceed 100")
    private int stateAssessmentELA;

    @Min(value = 0, message = "Math score must be non-negative")
    @Max(value = 100, message = "Math score must not exceed 100")
    private int stateAssessmentMath;

    public AcademicPerformanceDTO() {
    }

    public AcademicPerformanceDTO(UUID studentId, String semester, String course, BigDecimal grade, int stateAssessmentELA, int stateAssessmentMath) {
        this.studentId = studentId;
        this.semester = semester;
        this.course = course;
        this.grade = grade;
        this.stateAssessmentELA = stateAssessmentELA;
        this.stateAssessmentMath = stateAssessmentMath;
    }


    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public int getStateAssessmentELA() {
        return stateAssessmentELA;
    }

    public void setStateAssessmentELA(int stateAssessmentELA) {
        this.stateAssessmentELA = stateAssessmentELA;
    }

    public int getStateAssessmentMath() {
        return stateAssessmentMath;
    }

    public void setStateAssessmentMath(int stateAssessmentMath) {
        this.stateAssessmentMath = stateAssessmentMath;
    }
}
