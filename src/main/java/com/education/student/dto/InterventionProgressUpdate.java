package com.education.student.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;


public class InterventionProgressUpdate {
    @NotNull
    private BigDecimal currentScore;

    public InterventionProgressUpdate(BigDecimal currentScore) {
        this.currentScore = currentScore;
    }

    public BigDecimal getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(BigDecimal currentScore) {
        this.currentScore = currentScore;
    }
}
