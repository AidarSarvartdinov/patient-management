package com.pm.scheduling_service.infrastructure.messaging.inbox;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.pm.scheduling_service.application.inbox.ProcessedEvent;
import com.pm.scheduling_service.application.inbox.ProcessedEventRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepository {

    private final InboxRepository inboxRepository;

    @Override
    public boolean existsById(UUID eventId) {
        return inboxRepository.existsById(eventId);
    }

    @Override
    public void save(ProcessedEvent event) {
        inboxRepository.save(new Inbox(event.eventId(), event.createdAt()));
    }

    @Override
    public int deleteByCreatedAtBefore(LocalDateTime retentionDate) {
        return inboxRepository.deleteByCreatedAtBefore(retentionDate);
    }
    
}
