package com.bs.billing_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bs.billing_service.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/stripe/webhook")
@Tag(name = "Stripe Webhooks", description = "Endpoints for handling Stripe asynchronous messages")
public class StripeWebhookController {
    private final StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping
    @Operation(summary = "Handles Stripe event", description = "This endpoint is called by Stripe." +
            "\n **Important**: It is not intended for manual calling. Requires Stripe signature")
    public void handle(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature)
            throws SignatureVerificationException {
        stripeWebhookService.handle(payload, signature);
    }
}
