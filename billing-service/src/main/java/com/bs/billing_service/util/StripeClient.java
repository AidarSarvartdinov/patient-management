package com.bs.billing_service.util;

import org.springframework.stereotype.Component;

import com.bs.billing_service.dto.StripeSessionResult;
import com.bs.billing_service.model.Payment;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Component
public class StripeClient {
    public StripeSessionResult creteCheckoutSession(Payment payment) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://example.com/success")
                .setCancelUrl("http://example.com/cancel")
                .putMetadata("paymentId", payment.getId().toString())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(
                                1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder().setCurrency(payment.getCurrency())
                                        .setUnitAmount(payment.getAmount())
                                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Test Payment").build())
                                        .build())
                        .build())
                .build();

        Session session = Session.create(params);
        
        return new StripeSessionResult(session.getId(), session.getUrl());
    }
}
