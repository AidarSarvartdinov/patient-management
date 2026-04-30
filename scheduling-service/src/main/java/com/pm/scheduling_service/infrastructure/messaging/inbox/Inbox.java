package com.pm.scheduling_service.infrastructure.messaging.inbox;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Inbox {
    @Id
    private UUID eventId;

    public Inbox(UUID eventId) {
        this.eventId = eventId;
    }
}
