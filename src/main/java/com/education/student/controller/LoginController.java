package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.LoginPayload;
import com.education.student.dto.LoginRequest;
import com.education.student.jwt.JwtService;
import com.education.student.model.User;
import com.education.student.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/student")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class); // or UserController.class

    @Autowired
    JwtService jwtService;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginPayload>> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            log.info("Attempting login for user: {}", request.getUsername());
            User user = userService.authenticate(request.getUsername(), request.getPassword());

            String token = jwtService.generateToken(user.getUsername());

            Cookie cookie = new Cookie("authToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

            LoginPayload payload = new LoginPayload(
                    user.getId(),
                    user.getRole(),
                    LocalDateTime.now(),
                    token
            );

            log.info("Generated JWT token for user {}: {}", request.getUsername(), token);


            log.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful", payload));

        } catch (RuntimeException e) {
            log.warn("Login failed for user: {}. Reason: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Login failed", "LOGIN_FAILED", e.getMessage()));
        }
    }
}
