package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.LoginPayload;
import com.education.student.dto.LoginRequest;
import com.education.student.jwt.JwtService;
import com.education.student.model.Role;
import com.education.student.model.User;
import com.education.student.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @Mock
    JwtService jwtService;

    @Mock
    UserService userService;

    @Mock
    HttpServletResponse httpServletResponse;

    @InjectMocks
    LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("pass");

        User user = new User();
        user.setId("USER0001");
        user.setUsername("testuser");
        user.setPassword("pass");
        user.setRole(Role.TEACHER);

        when(userService.authenticate("testuser", "pass")).thenReturn(user);
        when(jwtService.generateToken("testuser")).thenReturn("jwt-token");

        ResponseEntity<ApiResponse<LoginPayload>> response = loginController.login(request, httpServletResponse);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isSuccess());
        assertEquals("USER0001", response.getBody().getData().getUserId());
        assertEquals("TEACHER", response.getBody().getData().getRole().name());
        assertEquals("jwt-token", response.getBody().getData().getToken());
        verify(httpServletResponse, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testLogin_Failure() {
        LoginRequest request = new LoginRequest();
        request.setUsername("failuser");
        request.setPassword("fail");

        when(userService.authenticate("failuser", "fail")).thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<ApiResponse<LoginPayload>> response = loginController.login(request, httpServletResponse);

        assertEquals(401, response.getStatusCode().value());
        assertFalse(response.getBody().isSuccess());
        assertEquals("LOGIN_FAILED", response.getBody().getError().getErrorCode());
        assertEquals("Invalid credentials", response.getBody().getError().getErrorMessage());
        verify(httpServletResponse, never()).addCookie(any(Cookie.class));
    }
}