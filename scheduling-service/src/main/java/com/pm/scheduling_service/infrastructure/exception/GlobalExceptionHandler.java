package com.pm.scheduling_service.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pm.scheduling_service.domain.exception.ConflictSlotsException;
import com.pm.scheduling_service.domain.exception.PaymentInitiationFailedException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(exception = UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(exception = ConflictSlotsException.class)
    public ResponseEntity<?> handleConflictSlots(ConflictSlotsException ex) {
        log.warn("Time slot conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("You have time slot conflict");
    }

    @ExceptionHandler(exception = PaymentInitiationFailedException.class)
    public ResponseEntity<?> handlePaymentInitiationFailed(PaymentInitiationFailedException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(400).body("Payment initiation failed");
    }

    @ExceptionHandler(exception = PaymentServiceUnavailableException.class)
    public ResponseEntity<?> handlePaymentServiceUnavailable(PaymentServiceUnavailableException ex) {
        log.error("Payment service unavailable: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body("Something went wrong");
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body("Something went wrong");
    }
}
