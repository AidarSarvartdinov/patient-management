package com.pm.scheduling_service.domain.exception;

public class PaymentInitiationFailedException extends RuntimeException {
    public PaymentInitiationFailedException(String message) {
        super(message);
    }
}
