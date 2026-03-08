package com.omnichannel.center.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex, HttpServletRequest request) {
        log.warn("ApiException at {} {} -> status={} message={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getStatus(),
                ex.getMessage(),
                ex);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(payload);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("ValidationException at {} {} -> {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);
        Map<String, Object> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "Validation error");
        payload.put("errors", errors);
        return ResponseEntity.badRequest().body(payload);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("MethodNotSupported at {} {} -> {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "Method not allowed");
        payload.put("detail", ex.getMessage());
        return ResponseEntity.status(405).body(payload);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex, HttpServletRequest request) {
        log.error("UnhandledException at {} {} -> {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "Internal server error");
        payload.put("detail", ex.getMessage());
        return ResponseEntity.status(500).body(payload);
    }
}
