package com.bs.billing_service.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bs.billing_service.enums.PaymentFailureReason;
import com.bs.billing_service.model.Payment;
import com.bs.billing_service.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

@Service
public class StripeWebhookService {
    private final PaymentRepository paymentRepository;
    private final String webhookSecret;

    public StripeWebhookService(PaymentRepository paymentRepository,
            @Value("${STRIPE_WEBHOOK_SECRET}") String webhookSecret) {
        this.paymentRepository = paymentRepository;
        this.webhookSecret = webhookSecret;
    }

    public void handle(String payload, String signature) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, signature, webhookSecret);

        if (!"checkout.session.completed".equals(event.getType())
                && !"checkout.session.expired".equals(event.getType())
                && !"payment_intent.payment_failed".equals(event.getType())) {
            return;
        }

        Session session = extractSession(event);

        UUID paymentId = UUID.fromString(session.getMetadata().get("paymentId"));

        switch (event.getType()) {
            case "checkout.session.completed" -> handleSuccess(paymentId);
            case "payment_intent.payment_failed" -> handleFailure(paymentId);
            case "checkout.session.expired" -> handleExpired(paymentId);
        }
    }

    private Session extractSession(Event event) {
        Optional<StripeObject> objectOpt = event.getDataObjectDeserializer().getObject();

        if (objectOpt.isPresent() && objectOpt.get() instanceof Session session) {
            return session;
        }

        String sessionId = extractObjectId(event);

        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new IllegalStateException(
                    "Failed to retrieve Stripe Session " + sessionId, e);
        }
    }

    private String extractObjectId(Event event) {
        String rawJson = event.getDataObjectDeserializer().getRawJson();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawJson);
            return root.get("id").asText();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse Stripe webhook raw JSON, eventId=" + event.getId(), e);
        }
    }

    @Transactional
    public void handleSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.markPaid();
        paymentRepository.save(payment);
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
