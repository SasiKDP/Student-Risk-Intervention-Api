package com.education.student.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;


public class CreateInterventionRequest {
    @NotNull
    private String studentId;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate targetCompletionDate;
    @NotNull
    private BigDecimal startScore;
    @NotNull
    private BigDecimal goalScore;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public BigDecimal getGoalScore() {
        return goalScore;
    }

    public void setGoalScore(BigDecimal goalScore) {
        this.goalScore = goalScore;
    }

    public CreateInterventionRequest(String studentId, LocalDate startDate, LocalDate targetCompletionDate, BigDecimal startScore, BigDecimal goalScore) {
        this.studentId = studentId;
        this.startDate = startDate;
        this.targetCompletionDate = targetCompletionDate;
        this.startScore = startScore;
        this.goalScore = goalScore;
    }
}
