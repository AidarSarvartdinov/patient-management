package com.pm.auth_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pm.auth_service.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(exception = UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNameNotFound(UsernameNotFoundException ex) {
        log.warn("User not found: " + ex.getMessage());
        ErrorResponse response = new ErrorResponse(404, "User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(exception = JwtEncodingException.class)
    public ResponseEntity<ErrorResponse> handleJwtEncodingException(JwtEncodingException ex) {
        log.warn("Error during jwt encoding: " + ex.getMessage());
        ErrorResponse response = new ErrorResponse(500, "Something went wrong");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
