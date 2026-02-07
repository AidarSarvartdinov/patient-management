package com.bs.billing_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bs.billing_service.enums.PaymentStatus;
import com.bs.billing_service.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByUserIdAndOrderIdAndStatus(UUID userId, UUID orderId, PaymentStatus status);
}
