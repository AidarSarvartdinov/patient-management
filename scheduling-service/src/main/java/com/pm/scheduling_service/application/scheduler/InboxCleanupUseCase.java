package com.pm.scheduling_service.application.scheduler;

import java.time.LocalDateTime;

import com.pm.scheduling_service.application.inbox.ProcessedEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InboxCleanupUseCase {
    private final ProcessedEventRepository processedEventRepository;

    public int execute(LocalDateTime retentionDate) {
        log.info("Cleaning up inbox records older than {}", retentionDate);
        int deletedCount = processedEventRepository.deleteByCreatedAtBefore(retentionDate);
        log.info("Deleted {} inbox records", deletedCount);
        return deletedCount;
    }
}
