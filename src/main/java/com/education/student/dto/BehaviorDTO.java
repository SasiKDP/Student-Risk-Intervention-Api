package com.education.student.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public class BehaviorDTO {
    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotBlank(message = "Semester is required")
    private String semester;

    @Min(value = 0, message = "Disciplinary actions cannot be negative")
    private int disciplinaryActions;

    @Min(value = 0, message = "Suspensions cannot be negative")
    private int suspensions;

    public BehaviorDTO() {}

    public BehaviorDTO(UUID studentId, String semester, int disciplinaryActions, int suspensions) {
        this.studentId = studentId;
        this.semester = semester;
        this.disciplinaryActions = disciplinaryActions;
        this.suspensions = suspensions;
    }

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public int getDisciplinaryActions() { return disciplinaryActions; }
    public void setDisciplinaryActions(int disciplinaryActions) { this.disciplinaryActions = disciplinaryActions; }
    public int getSuspensions() { return suspensions; }
    public void setSuspensions(int suspensions) { this.suspensions = suspensions; }
}