package com.education.student.service;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.StudentResponse;
import com.education.student.dto.UserDto;
import com.education.student.exceptions.ResourceNotFoundException;
import com.education.student.model.Role;
import com.education.student.model.Students;
import com.education.student.repository.StudentRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    UserService userService;

    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(Students student) {
        try {
            String shortId = RandomStringUtils.randomAlphanumeric(8);
            UUID studentId = UUID.nameUUIDFromBytes(shortId.getBytes());
            student.setId(studentId);
            student.setCreatedAt(LocalDateTime.now());

            Students saved = studentRepository.save(student);

            UserDto userDto = new UserDto();

            userDto.setUsername(saved.getName());
            userDto.setPassword(saved.getName() + "@123");
            userDto.setRole(Role.STUDENT);
            userService.createUser(userDto);

            StudentResponse response = new StudentResponse(saved.getId().toString(), saved.getName(), saved.getGrade());

            logger.info("Student created successfully with ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Student created successfully", response));
        } catch (Exception e) {
            logger.error("Failed to create student: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create student.", "INTERNAL_ERROR", e.getMessage()));
        }
    }

    public ResponseEntity<ApiResponse<List<Students>>> getAllStudents() {
        try {
            List<Students> students = studentRepository.findAll();
            logger.info("Retrieved {} students", students.size());
            return ResponseEntity.ok(ApiResponse.success("List of all students retrieved", students));
        } catch (Exception e) {
            logger.error("Error fetching all students", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve student list.", "INTERNAL_ERROR", e.getMessage()));
        }
    }

    public ResponseEntity<ApiResponse<Students>> getStudentById(UUID studentId) {
        try {
            Students student = studentRepository.findById(studentId)
                    .orElseThrow(() -> {
                        logger.warn("Student not found with ID: {}", studentId);
                        return new ResourceNotFoundException("Student not found with ID: " + studentId);
                    });
            logger.info("Student retrieved with ID: {}", studentId);
            return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "STUDENT_NOT_FOUND", null));
        } catch (Exception e) {
            logger.error("Error retrieving student by ID: {}", studentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve student.", "INTERNAL_ERROR", e.getMessage()));
        }
    }
}
