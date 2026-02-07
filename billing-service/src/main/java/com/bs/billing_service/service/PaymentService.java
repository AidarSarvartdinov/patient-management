package com.bs.billing_service.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bs.billing_service.dto.CreatePaymentRequest;
import com.bs.billing_service.dto.CreatePaymentResponse;
import com.bs.billing_service.dto.StripeSessionResult;
import com.bs.billing_service.enums.PaymentStatus;
import com.bs.billing_service.model.Payment;
import com.bs.billing_service.repository.PaymentRepository;
import com.bs.billing_service.util.StripeClient;
import com.stripe.exception.StripeException;

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
                .findByUserIdAndOrderIdAndStatus(request.userId(), request.orderId(), PaymentStatus.NEW)
                .orElse(createNewPayment(request));

        if (payment.getStripeSessionId() != null) {
            return new CreatePaymentResponse(payment.getId().toString(), payment.getStripeSessionUrl());
        }
        StripeSessionResult session = createCheckoutSession(payment);
        payment = markPending(payment.getId(), session);

        return new CreatePaymentResponse(payment.getId().toString(), session.url());
    }

    @Transactional
    public Payment createNewPayment(CreatePaymentRequest request) {
        Payment payment = new Payment(request.amount(), request.currency(), request.userId(), request.orderId());
        return paymentRepository.save(payment);
    }

    private StripeSessionResult createCheckoutSession(Payment payment) throws StripeException {
        return stripeClient.creteCheckoutSession(payment);
    }

    @Transactional
    public Payment markPending(UUID paymentId, StripeSessionResult session) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        Payment payment = optionalPayment.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        payment.markPending(session.id(), session.url());
        return paymentRepository.save(payment);
    }

}
