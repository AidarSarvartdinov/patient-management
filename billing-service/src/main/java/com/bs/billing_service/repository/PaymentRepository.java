package com.bs.billing_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bs.billing_service.enums.PaymentStatus;
import com.bs.billing_service.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByUserIdAndOrderIdAndStatus(UUID userId, UUID orderId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt <= :threshold")
    List<Payment> findOldPendingPayments(@Param("threshold") LocalDateTime threshold);
}
