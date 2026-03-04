package com.bs.billing_service.controller;

import org.springframework.web.bind.annotation.RestController;

import com.bs.billing_service.dto.CreatePaymentRequest;
import com.bs.billing_service.dto.CreatePaymentResponse;
import com.bs.billing_service.service.PaymentService;
import com.stripe.exception.StripeException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment", description = "API for payments management")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Creates a new payment")
    public ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest request)
            throws StripeException {
        CreatePaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

}
