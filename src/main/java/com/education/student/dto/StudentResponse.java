package com.education.student.dto;

public class StudentResponse {
    private String id;
    private String name;
    private String grade;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public StudentResponse(String id, String name, String grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }
}
