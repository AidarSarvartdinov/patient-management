package com.bs.billing_service.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bs.billing_service.kafka.KafkaProducer;
import com.bs.billing_service.model.Outbox;
import com.bs.billing_service.repository.OutboxRepository;

@Component
public class OutboxScheduler {
    private final KafkaProducer kafkaProducer;

    private final OutboxRepository outboxRepository;

    private static final Logger log = LoggerFactory.getLogger(OutboxScheduler.class);

    public OutboxScheduler(KafkaProducer kafkaProducer, OutboxRepository outboxRepository) {
        this.kafkaProducer = kafkaProducer;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void sendEvents() {
        List<Outbox> events = outboxRepository.findAll();

        if (events.isEmpty()) {
            return;
        }

        List<Outbox> sentEvents = new ArrayList<>();

        for (Outbox event : events) {
            try {
                kafkaProducer.sendEvent(event);
                sentEvents.add(event);
            } catch (Exception e) {
                log.error("Error sending event to Kafka", e);
                break;
            }
        }

        if (!sentEvents.isEmpty()) {
            outboxRepository.deleteAllInBatch(sentEvents);
        }
    }
}
