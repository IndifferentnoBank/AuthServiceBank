package com.bank.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> resolveResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        val body = new LinkedHashMap<String, Object>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode());
        body.put("message", ex.getReason());
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, ex.getStatusCode());
    }
}
