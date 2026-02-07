package com.bs.billing_service.dto;

public record CreatePaymentResponse(
        String paymentId,
        String sessionUrl) {
}
