package com.bs.billing_service.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bs.billing_service.dto.CreatePaymentRequest;
import com.bs.billing_service.dto.StripeSessionResult;
import com.bs.billing_service.enums.PaymentFailureReason;
import com.bs.billing_service.model.Outbox;
import com.bs.billing_service.model.Payment;
import com.bs.billing_service.repository.OutboxRepository;
import com.bs.billing_service.repository.PaymentRepository;

import jakarta.persistence.EntityNotFoundException;
import payment.events.PaymentEvent;

@Service
public class PaymentTransactionService {
    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentTransactionService(PaymentRepository paymentRepository, OutboxRepository outboxRepository) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public Payment createNewPayment(CreatePaymentRequest request) {
        Payment payment = new Payment(request.amount(), request.currency(), request.userId(), request.orderId());
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markPending(UUID paymentId, StripeSessionResult session) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        Payment payment = optionalPayment.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        payment.markPending(session.id(), session.url());
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment handleSuccess(UUID paymentId) {
        log.info("Handling payment success");
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.markPaid();
        payment = paymentRepository.save(payment);

        try {
            PaymentEvent paymentEvent = PaymentEvent.newBuilder()
                    .setUserId(payment.getUserId().toString())
                    .setOrderId(payment.getOrderId().toString())
                    .setPaymentId(payment.getId().toString())
                    .setEventType("PAYMENT_SUCCESS")
                    .setEventId(UUID.randomUUID().toString())
                    .build();

            outboxRepository.save(new Outbox("payments", payment.getOrderId().toString(), paymentEvent.toByteArray()));
        } catch (Exception e) {
            log.error("Failed to serialize outbox payload", e);
            throw new RuntimeException("Serialization error during outbox write", e);
        }

        return payment;
    }

    @Transactional
    public void handleFailure(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.markFailed(PaymentFailureReason.PAYMENT_FAILED);
    }

    @Transactional
    public void handleExpired(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.markFailed(PaymentFailureReason.SESSION_EXPIRED);
    }
}
