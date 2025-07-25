package com.education.student.dto;

import com.education.student.model.Role;

import java.time.LocalDateTime;

public class LoginPayload {
    private String userId;
    private Role role;
    private LocalDateTime loginTimestamp;
    private String token;

    public LoginPayload() {}

    public LoginPayload(String userId, Role role, LocalDateTime loginTimestamp, String token) {
        this.userId = userId;
        this.role = role;
        this.loginTimestamp = loginTimestamp;
        this.token = token;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getLoginTimestamp() { return loginTimestamp; }
    public void setLoginTimestamp(LocalDateTime loginTimestamp) { this.loginTimestamp = loginTimestamp; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
