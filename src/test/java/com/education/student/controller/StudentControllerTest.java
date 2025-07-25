package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.model.Students;
import com.education.student.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StudentControllerTest {

    @Mock
    StudentService studentService;

    @InjectMocks
    StudentController studentController;

    List<Students> studentsList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        studentsList = new ArrayList<>();
        Students s1 = new Students();
        s1.setId(UUID.fromString("36fa2214-4ae1-3e5f-9d82-568dbb6fd708"));
        s1.setName("naveen");
        studentsList.add(s1);

        Students s2 = new Students();
        s2.setId(UUID.fromString("d43b9a06-3f14-3ec6-8804-5681da0e72cb"));
        s2.setName("kumar");
        studentsList.add(s2);

        // Add more students as needed for your test
    }

    @Test
    void testAllStudentsList_ReturnsStudentList() {
        ApiResponse<List<Students>> apiResponse = ApiResponse.success("List of all students retrieved", studentsList);
        when(studentService.getAllStudents()).thenReturn(ResponseEntity.ok(apiResponse));

        ResponseEntity<ApiResponse<List<Students>>> response = studentController.allStudentsList();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("naveen", response.getBody().getData().get(0).getName());
        assertEquals("kumar", response.getBody().getData().get(1).getName());
    }
}