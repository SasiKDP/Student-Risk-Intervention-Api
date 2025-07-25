package com.education.student.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class RoleAccessDeniedException extends AccessDeniedException {
    public RoleAccessDeniedException(String message) {
        super(message);
    }
}

