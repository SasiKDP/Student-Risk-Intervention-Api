package com.education.student.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interventions")
public class Intervention {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private Students student;

    @NotBlank(message = "Intervention type is required")
    @Size(max = 100, message = "Intervention type must not exceed 100 characters")
    private String interventionType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Target completion date is required")
    private LocalDate targetCompletionDate;

    @DecimalMin(value = "0.00", message = "Start score must be non-negative")
    private BigDecimal startScore;

    @DecimalMin(value = "0.00", message = "Current score must be non-negative")
    private BigDecimal currentScore;

    @DecimalMin(value = "0.00", message = "Goal score must be non-negative")
    private BigDecimal goalScore;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private InterventionStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum InterventionStatus {
        ON_TRACK, NOT_ON_TRACK, COMPLETED
    }

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

    public String getInterventionType() {
        return interventionType;
    }

    public void setInterventionType(String interventionType) {
        this.interventionType = interventionType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(LocalDate targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public BigDecimal getStartScore() {
        return startScore;
    }

    public void setStartScore(BigDecimal startScore) {
        this.startScore = startScore;
    }

    public BigDecimal getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(BigDecimal currentScore) {
        this.currentScore = currentScore;
    }

    public BigDecimal getGoalScore() {
        return goalScore;
    }

    public void setGoalScore(BigDecimal goalScore) {
        this.goalScore = goalScore;
    }

    public InterventionStatus getStatus() {
        return status;
    }

    public void setStatus(InterventionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
