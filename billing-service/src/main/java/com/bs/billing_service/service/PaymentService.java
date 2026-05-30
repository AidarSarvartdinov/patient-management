package com.bs.billing_service.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bs.billing_service.dto.CreatePaymentRequest;
import com.bs.billing_service.dto.CreatePaymentResponse;
import com.bs.billing_service.dto.StripeSessionResult;
import com.bs.billing_service.enums.PaymentStatus;
import com.bs.billing_service.model.Payment;
import com.bs.billing_service.repository.PaymentRepository;
import com.bs.billing_service.util.StripeClient;
import com.stripe.exception.StripeException;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;
    private final PaymentTransactionService paymentTransactionService;
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository paymentRepository, StripeClient stripeClient,
            PaymentTransactionService paymentTransactionService) {
        this.paymentRepository = paymentRepository;
        this.stripeClient = stripeClient;
        this.paymentTransactionService = paymentTransactionService;
    }

    public CreatePaymentResponse createPayment(CreatePaymentRequest request) throws StripeException {
        log.info("Received CreatePaymentRequest : userId=" + request.userId() + ", orderId=" + request.orderId());
        Payment payment = paymentRepository
                .findByUserIdAndOrderIdAndStatus(request.userId(), request.orderId(), PaymentStatus.PENDING)
                .orElseGet(() -> paymentTransactionService.createNewPayment(request));

        if (payment.getStripeSessionId() != null) {
            return new CreatePaymentResponse(payment.getId().toString(), payment.getStripeSessionUrl());
        }

        log.info("Creating Stripe session for payment {}", payment.getId());
        StripeSessionResult session = stripeClient.createCheckoutSession(payment);
        payment = paymentTransactionService.markPending(payment.getId(), session);

        return new CreatePaymentResponse(payment.getId().toString(), session.url());
    }

    public void processStripeEvent(String eventType, UUID paymentId) {
        switch (eventType) {
            case "checkout.session.completed" -> paymentTransactionService.handleSuccess(paymentId);
            case "payment_intent.payment_failed" -> paymentTransactionService.handleFailure(paymentId);
            case "checkout.session.expired" -> paymentTransactionService.handleExpired(paymentId);
        }
    }
}
