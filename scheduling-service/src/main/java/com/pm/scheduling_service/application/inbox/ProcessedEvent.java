package com.pm.scheduling_service.application.inbox;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProcessedEvent(UUID eventId, LocalDateTime createdAt) {
    public ProcessedEvent(UUID eventId) {
        this(eventId, LocalDateTime.now());
    }
}
