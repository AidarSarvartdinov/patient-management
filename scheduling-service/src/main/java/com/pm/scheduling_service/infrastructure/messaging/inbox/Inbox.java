package com.pm.scheduling_service.infrastructure.messaging.inbox;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Inbox {
    @Id
    private UUID eventId;

    @NotNull
    private LocalDateTime createdAt;

    public Inbox(UUID eventId) {
        this.eventId = eventId;
        this.createdAt = LocalDateTime.now();
    }
}
