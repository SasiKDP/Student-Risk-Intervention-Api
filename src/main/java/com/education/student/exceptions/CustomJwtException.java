package com.education.student.exceptions;

public class CustomJwtException extends RuntimeException {
    public CustomJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
