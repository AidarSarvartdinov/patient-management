package com.bs.billing_service.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bs.billing_service.enums.PaymentFailureReason;
import com.bs.billing_service.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "user_id")
    private UUID userId;

    @NotNull
    @Column(name = "order_id")
    private UUID orderId;

    @NotNull
    private Long amount;

    @NotNull
    private String currency;


    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "stripe_session_url", columnDefinition = "TEXT")
    private String stripeSessionUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private PaymentFailureReason failureReason;

    @NotNull
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Version
    private Long version;

    public Payment() {}

    public Payment(Long amount, String currency, UUID userId, UUID orderId) {
        this.amount = amount;
        this.currency = currency;
        this.userId = userId;
        this.orderId = orderId;
        this.status = PaymentStatus.NEW;
        this.createAt = LocalDateTime.now();
 
    }

    public void markPending(String stripeSessionId, String stripeSessioUrl) {
        if (this.status != PaymentStatus.NEW) {
            throw new IllegalStateException("Invalid transition");
        }

        this.status = PaymentStatus.PENDING;
        this.stripeSessionId = stripeSessionId;
        this.stripeSessionUrl = stripeSessioUrl;
    }

    public void markPaid() {
        if (this.status == PaymentStatus.PAID) return;
        if (this.status != PaymentStatus.PENDING && this.status != PaymentStatus.NEW) {
            throw new IllegalStateException("Invalid transition");
        }

        this.status = PaymentStatus.PAID;
    }

    public void markFailed(PaymentFailureReason reason) {
        if (this.status == PaymentStatus.FAILED) return;
        if (this.status != PaymentStatus.PENDING && this.status != PaymentStatus.NEW) {
            throw new IllegalStateException("Invalid transition");
        }

        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public String getStripeSessionUrl() {
        return stripeSessionUrl;
    }
}
