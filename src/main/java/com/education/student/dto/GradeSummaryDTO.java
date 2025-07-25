package com.education.student.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeSummaryDTO implements Serializable {
    private String studentName;
    private String overallGrade;
    private double overallAverage;
    private Map<String, String> semesterGrades;

    public GradeSummaryDTO() {}

    public GradeSummaryDTO(String studentName, String overallGrade, double overallAverage, Map<String, String> semesterGrades) {
        this.studentName = studentName;
        this.overallGrade = overallGrade;
        this.overallAverage = overallAverage;
        this.semesterGrades = semesterGrades;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getOverallGrade() {
        return overallGrade;
    }

    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }

    public double getOverallAverage() {
        return overallAverage;
    }

    public void setOverallAverage(double overallAverage) {
        this.overallAverage = overallAverage;
    }

    public Map<String, String> getSemesterGrades() {
        return semesterGrades;
    }

    public void setSemesterGrades(Map<String, String> semesterGrades) {
        this.semesterGrades = semesterGrades;
    }
}