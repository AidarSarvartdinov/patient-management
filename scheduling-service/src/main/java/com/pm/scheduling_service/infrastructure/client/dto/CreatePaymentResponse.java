package com.pm.scheduling_service.infrastructure.client.dto;

public record CreatePaymentResponse(
        String paymentId,
        String sessionUrl) {
}

