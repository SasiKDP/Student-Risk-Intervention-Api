package com.education.student.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.UUID;

@Entity
@Table(name = "behavior")
public class Behavior {

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

    @Min(value = 0, message = "Disciplinary actions must be zero or positive")
    private int disciplinaryActions;

    @Min(value = 0, message = "Suspensions must be zero or positive")
    private int suspensions;

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

    public int getDisciplinaryActions() {
        return disciplinaryActions;
    }

    public void setDisciplinaryActions(int disciplinaryActions) {
        this.disciplinaryActions = disciplinaryActions;
    }

    public int getSuspensions() {
        return suspensions;
    }

    public void setSuspensions(int suspensions) {
        this.suspensions = suspensions;
    }
}
