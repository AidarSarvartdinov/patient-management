package com.pm.scheduling_service.infrastructure.messaging;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.scheduling_service.domain.service.SlotService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import payment.events.PaymentEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventsConsumer {
    private final SlotService slotService;

    @KafkaListener(topics = "payment", groupId = "scheduling-service")
    public void handlePaymentEvent(byte[] event) {
        PaymentEvent paymentEvent;
        try {
            paymentEvent = PaymentEvent.parseFrom(event);
            switch (paymentEvent.getEventType()) {
                case "PAYMENT_SUCCESS" -> slotService.confirmSlotByPayment(
                        UUID.fromString(paymentEvent.getOrderId()),
                        UUID.fromString(paymentEvent.getUserId()));

                default -> log.warn("Unknown event type {}", paymentEvent.getEventType());
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event: ", e);
        }
        
    }
}
