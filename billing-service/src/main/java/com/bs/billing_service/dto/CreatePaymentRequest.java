package com.bs.billing_service.dto;

import java.util.UUID;

public record CreatePaymentRequest(
        UUID userId,
        UUID orderId,
        long amount,
        String currency) {
}
