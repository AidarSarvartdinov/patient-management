package com.bs.billing_service.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private final String webhookSecret;
    private final PaymentService paymentService;
    private static final Logger log = LoggerFactory.getLogger(StripeWebhookService.class);

    public StripeWebhookService(@Value("${STRIPE_WEBHOOK_SECRET}") String webhookSecret, PaymentService paymentService) {
        this.webhookSecret = webhookSecret;
        this.paymentService = paymentService;
    }

    public void handle(String payload, String signature) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, signature, webhookSecret);

        if (!"checkout.session.completed".equals(event.getType())
                && !"checkout.session.expired".equals(event.getType())
                && !"payment_intent.payment_failed".equals(event.getType())) {
            return;
        }

        log.info("Got Stripe event {}", event.getType());

        Session session = extractSession(event);

        UUID paymentId = UUID.fromString(session.getMetadata().get("paymentId"));

        paymentService.processStripeEvent(event.getType(), paymentId);        
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

}
