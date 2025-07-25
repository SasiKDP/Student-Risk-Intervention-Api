package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.UserDto;
import com.education.student.dto.UserResponseDto;
import com.education.student.model.Role;
import com.education.student.model.User;
import com.education.student.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        UserDto dto = new UserDto();
        dto.setUsername("testuser");
        dto.setPassword("pass");
        dto.setRole(Role.TEACHER);

        UserResponseDto responseDto = new UserResponseDto("USER0001", "testuser", Role.TEACHER);
        when(userService.createUser(dto)).thenReturn(responseDto);

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.createUser(dto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isSuccess());
        assertEquals("testuser", response.getBody().getData().getUsername());
    }

    @Test
    void testCreateUser_Failure() {
        UserDto dto = new UserDto();
        dto.setUsername("failuser");
        dto.setPassword("fail");
        dto.setRole(Role.TEACHER);

        when(userService.createUser(dto)).thenThrow(new RuntimeException("Registration error"));

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.createUser(dto);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertFalse(response.getBody().isSuccess());
        assertEquals("REGISTRATION_FAILED", response.getBody().getError().getErrorCode());
    }

    @Test
    void testGetAllUsers_Success() {
        User user = new User();
        user.setId("USER0001");
        user.setUsername("testuser");
        user.setPassword("pass");
        user.setRole(Role.TEACHER);

        when(userService.getAllUsersList()).thenReturn(List.of(user));

        ResponseEntity<ApiResponse<List<User>>> response = userController.getAllUsers();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("testuser", response.getBody().getData().get(0).getUsername());
    }

    @Test
    void testGetAllUsers_Failure() {
        when(userService.getAllUsersList()).thenThrow(new RuntimeException("Fetch error"));

        ResponseEntity<ApiResponse<List<User>>> response = userController.getAllUsers();

        assertTrue(response.getStatusCode().is5xxServerError());
        assertFalse(response.getBody().isSuccess());
        assertEquals("FETCH_FAILED", response.getBody().getError().getErrorCode());
    }
}