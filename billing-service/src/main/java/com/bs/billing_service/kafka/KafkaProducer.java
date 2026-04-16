package com.bs.billing_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.bs.billing_service.model.Outbox;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> template;
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, byte[]> template) {
        this.template = template;
    }

    public void sendEvent(Outbox outbox) {
        log.info("Send event to Kafka with topic {} and key {}", outbox.getTopic(), outbox.getKey());
        template.send(outbox.getTopic(), outbox.getKey(), outbox.getPayload());
    }
}
