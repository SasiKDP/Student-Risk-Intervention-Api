package com.education.student.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "academic_performance")
public class AcademicPerformance {

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

    @NotBlank(message = "Course is required")
    @Size(max = 100, message = "Course must not exceed 100 characters")
    private String course;

    @NotNull(message = "Grade is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Grade must be at least 0.00")
    @DecimalMax(value = "100.00", inclusive = true, message = "Grade must not exceed 100.00")
    private BigDecimal grade;

    @Min(value = 0, message = "ELA score must be non-negative")
    @Max(value = 1000, message = "ELA score must not exceed 1000")
    @Column(name = "state_assessment_ela")
    private int stateAssessmentELA;

    @Min(value = 0, message = "Math score must be non-negative")
    @Max(value = 1000, message = "Math score must not exceed 1000")
    @Column(name = "state_assessment_math")
    private int stateAssessmentMath;


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
