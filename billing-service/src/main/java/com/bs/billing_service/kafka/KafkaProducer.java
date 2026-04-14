package com.bs.billing_service.kafka;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import payment.events.PaymentEvent;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> template;
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, byte[]> template) {
        this.template = template;
    }

    public void sendPaymentSuccessEvent(UUID userId, UUID orderId) {
        PaymentEvent event = PaymentEvent.newBuilder()
                .setUserId(userId.toString())
                .setOrderId(orderId.toString())
                .setEventType("PAYMENT_SUCCESS")
                .build();

        try {
            template.send("payment", orderId.toString(), event.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PaymentSuccess event: " + event, e);
        }
    }
}
