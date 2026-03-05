package com.bs.billing_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bs.billing_service.dto.CreatePaymentRequest;
import com.bs.billing_service.dto.CreatePaymentResponse;
import com.bs.billing_service.dto.StripeSessionResult;
import com.bs.billing_service.enums.PaymentFailureReason;
import com.bs.billing_service.enums.PaymentStatus;
import com.bs.billing_service.model.Payment;
import com.bs.billing_service.repository.PaymentRepository;
import com.bs.billing_service.util.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository paymentRepository, StripeClient stripeClient) {
        this.paymentRepository = paymentRepository;
        this.stripeClient = stripeClient;
    }

    public CreatePaymentResponse createPayment(CreatePaymentRequest request) throws StripeException {
        log.info("Received CreatePaymentRequest : userId=" + request.userId() + ", orderId=" + request.orderId());
        Payment payment = paymentRepository
                .findByUserIdAndOrderIdAndStatus(request.userId(), request.orderId(), PaymentStatus.PENDING)
                .orElseGet(() -> createNewPayment(request));

        if (payment.getStripeSessionId() != null) {
            return new CreatePaymentResponse(payment.getId().toString(), payment.getStripeSessionUrl());
        }

        StripeSessionResult session = stripeClient.createCheckoutSession(payment);
        payment = markPending(payment.getId(), session);

        return new CreatePaymentResponse(payment.getId().toString(), session.url());
    }

    @Transactional
    public Payment createNewPayment(CreatePaymentRequest request) {
        Payment payment = new Payment(request.amount(), request.currency(), request.userId(), request.orderId());
        return paymentRepository.save(payment);
    }

    public void processStripeEvent(String eventType, UUID paymentId) {
        switch (eventType) {
            case "checkout.session.completed" -> handleSuccess(paymentId);
            case "payment_intent.payment_failed" -> handleFailure(paymentId);
            case "checkout.session.expired" -> handleExpired(paymentId);
        }
    }

    @Transactional
    public Payment markPending(UUID paymentId, StripeSessionResult session) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        Payment payment = optionalPayment.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        payment.markPending(session.id(), session.url());
        return payment;
    }

    @Transactional
    public void handleSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.markPaid();
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


    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void synchronizePaymentStatus() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(20);
        List<Payment> pendingPayments = paymentRepository.findOldPendingPayments(threshold);

        for (Payment payment : pendingPayments) {
            if (payment.getStripeSessionId() != null) {
                try {
                    Session session = Session.retrieve(payment.getStripeSessionId());
                    if (session.getPaymentStatus().equals("paid")) {
                        handleSuccess(payment.getId());
                    } else if (session.getStatus().equals("expired")) {
                        handleExpired(payment.getId());
                    }
                } catch (StripeException ex) {
                    log.error("Failed to retrieve Stripe Session " + payment.getStripeSessionId() + "Error: "
                            + ex.getMessage());
                }
            }
        }
    }

}
