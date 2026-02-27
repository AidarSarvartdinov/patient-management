package com.pm.scheduling_service.domain.exception;

public class ConflictSlotsException extends RuntimeException {
    public ConflictSlotsException(String message) {
        super(message);
    }
}
