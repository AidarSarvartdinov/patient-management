package com.pm.scheduling_service.infrastructure.messaging;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.scheduling_service.application.service.ConfirmBookingUseCase;
import com.pm.scheduling_service.infrastructure.messaging.inbox.InboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import payment.events.PaymentEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventsConsumer {
    private final ConfirmBookingUseCase confirmBookingUseCase;
    private final InboxRepository inboxRepository;

    @KafkaListener(topics = "payments", groupId = "scheduling-service")
    public void handlePaymentEvent(byte[] event) {
        PaymentEvent paymentEvent;
        try {
            paymentEvent = PaymentEvent.parseFrom(event);

            if (inboxRepository.existsById(UUID.fromString(paymentEvent.getEventId()))) {
                return;
            }

            switch (paymentEvent.getEventType()) {
                case "PAYMENT_SUCCESS" ->
                    confirmBookingUseCase.execute(
                            UUID.fromString(paymentEvent.getOrderId()),
                            UUID.fromString(paymentEvent.getUserId()),
                            UUID.fromString(paymentEvent.getEventId()));

                default -> log.warn("Unknown event type {}", paymentEvent.getEventType());
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event: ", e);
        }

    }
}
