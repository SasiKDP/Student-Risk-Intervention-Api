package com.education.student.dto;

import lombok.Data;

@Data

public class InterventionRecommendation {
    private String studentName;
    private String recommendedType;
    private String reason;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRecommendedType() {
        return recommendedType;
    }

    public void setRecommendedType(String recommendedType) {
        this.recommendedType = recommendedType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InterventionRecommendation(String studentName, String recommendedType, String reason) {
        this.studentName = studentName;
        this.recommendedType = recommendedType;
        this.reason = reason;
    }
}
