package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.StudentResponse;
import com.education.student.model.Students;
import com.education.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;


    @PostMapping("/addStudent")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@RequestBody Students student) {
        return studentService.createStudent(student);
    }

    @GetMapping("/getAllStudents")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Students>>> allStudentsList() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'PARENT')")
    public ResponseEntity<ApiResponse<Students>> getStudentById(@PathVariable UUID id) {
        return studentService.getStudentById(id);
    }
}
