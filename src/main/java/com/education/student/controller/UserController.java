package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.UserDto;
import com.education.student.dto.UserResponseDto;
import com.education.student.model.User;
import com.education.student.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/student")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@RequestBody UserDto dto) {
        try {
            UserResponseDto createdUser = userService.createUser(dto);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", createdUser));
        } catch (RuntimeException e) {
            log.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Registration failed", "REGISTRATION_FAILED", e.getMessage()));
        }
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsersList();
            return ResponseEntity.ok(ApiResponse.success("List of all users retrieved", users));
        } catch (Exception e) {
            log.error("Error fetching user list: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve users", "FETCH_FAILED", e.getMessage()));
        }
    }
}
