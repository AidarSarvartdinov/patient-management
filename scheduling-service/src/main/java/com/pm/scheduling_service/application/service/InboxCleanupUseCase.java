package com.pm.scheduling_service.application.service;

import java.time.LocalDateTime;

import com.pm.scheduling_service.infrastructure.messaging.inbox.InboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InboxCleanupUseCase {
    private final InboxRepository inboxRepository;

    public int execute(LocalDateTime retentionDate) {
        log.info("Cleaning up inbox records older than {}", retentionDate);
        int deletedCount = inboxRepository.deleteByCreatedAtBefore(retentionDate);
        log.info("Deleted {} inbox records", deletedCount);
        return deletedCount;
    }
}
