package com.education.student.dto;

import com.education.student.model.RiskLevel;


public class StudentRiskAssessmentDto {
    private String studentId;
    private int riskScore;
    private RiskLevel riskLevel;
    private String semester;

    public StudentRiskAssessmentDto(String studentId, int riskScore, RiskLevel riskLevel, String semester) {
        this.studentId = studentId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.semester = semester;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}