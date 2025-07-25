package com.education.student.dto;

import com.education.student.model.RiskLevel;


public class AtRiskStudentDto {
    private String studentId;
    private String name;
    private RiskLevel riskLevel;
    private int riskScore;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public AtRiskStudentDto(String studentId, String name, RiskLevel riskLevel, int riskScore) {
        this.studentId = studentId;
        this.name = name;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
    }
}