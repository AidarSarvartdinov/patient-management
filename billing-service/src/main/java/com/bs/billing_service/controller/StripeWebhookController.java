package com.bs.billing_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bs.billing_service.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;

@RestController
@RequestMapping("/stripe/webhook")
public class StripeWebhookController {
    private final StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping
    public void handle(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature)
            throws SignatureVerificationException {
        stripeWebhookService.handle(payload, signature);
    }
}
