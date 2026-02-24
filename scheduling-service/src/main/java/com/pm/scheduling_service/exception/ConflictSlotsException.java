package com.pm.scheduling_service.exception;

public class ConflictSlotsException extends RuntimeException {
    public ConflictSlotsException(String message) {
        super(message);
    }
}
