package com.education.student.exceptions;

import com.education.student.dto.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        return buildError(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "VALIDATION_FAILED");
        response.put("message", fieldErrors);
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleAccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleRoleAccessDenied(RoleAccessDeniedException ex) {
        return buildError(String.valueOf(HttpStatus.FORBIDDEN.value()), "You don't have Permission to access this resource.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleSpringAccessDenied(AccessDeniedException ex) {
        return handleRoleAccessDenied(new RoleAccessDeniedException(ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {
        return buildError(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<?>> buildError(String code, String message, HttpStatus status) {
        return new ResponseEntity<>(ApiResponse.error("Request failed", code, message), status);
    }

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(CustomJwtException ex) {
        ApiResponse<Object> response = ApiResponse.error(
                ex.getMessage(),
                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                "Access denied due to invalid or expired token."
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


//    @ExceptionHandler(InvalidFormatException.class)
//    public ResponseEntity<ApiResponse<?>> handleInvalidFormatException(InvalidFormatException ex) {
//        String fieldName = "Unknown";
//        String invalidValue = "Invalid";
//
//        if (!ex.getPath().isEmpty()) {
//            fieldName = ex.getPath().get(0).getFieldName();
//            invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
//        }
//
//        String message = String.format("Invalid value '%s' for field '%s'.", invalidValue, fieldName);
//
//        return buildError(String.valueOf(HttpStatus.BAD_REQUEST.value()), message, HttpStatus.BAD_REQUEST);
//    }



// inside your GlobalExceptionHandler class

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String field = "";
            if (!ife.getPath().isEmpty()) {
                field = ife.getPath().get(0).getFieldName();
            }

            String message = "Invalid value for field '" + field + "'.";

            Class<?> targetType = ife.getTargetType();

            if (targetType.isEnum()) {
                // dynamically get enum constants as a comma-separated string
                String allowedValues =
                        java.util.Arrays.stream(targetType.getEnumConstants())
                                .map(Object::toString)
                                .collect(Collectors.joining(", "));
                message = String.format("Invalid value for %s. Allowed values: %s", field, allowedValues);
            }

            return buildError(String.valueOf(HttpStatus.BAD_REQUEST.value()), message, HttpStatus.BAD_REQUEST);
        }

        return buildError(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Malformed JSON request", HttpStatus.BAD_REQUEST);
    }

}
