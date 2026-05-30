package com.pm.scheduling_service.application.inbox;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProcessedEventRepository {
    boolean existsById(UUID eventId);
    void save(ProcessedEvent event);
    int deleteByCreatedAtBefore(LocalDateTime retentionDate);
}
