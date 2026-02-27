package com.pm.scheduling_service.infrastructure.client.dto;

import java.util.UUID;

public record CreatePaymentRequest(
        UUID userId,
        UUID orderId,
        long amount,
        String currency) {
}
