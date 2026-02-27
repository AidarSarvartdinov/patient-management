package com.pm.scheduling_service.domain.port;

import java.util.UUID;

public interface PaymentGateway {
    String initiatePayment(UUID patientId, UUID slotId, long price, String currency);
}
