package com.pm.scheduling_service.infrastructure.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pm.scheduling_service.application.service.InboxCleanupUseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InboxCleanupScheduler {
    private final InboxCleanupUseCase inboxCleanupUseCase;

    // every day at 3:00
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanUpOldMessages() {
        LocalDateTime retentionDate = LocalDateTime.now().minusDays(30);
        inboxCleanupUseCase.execute(retentionDate);
    }
}
