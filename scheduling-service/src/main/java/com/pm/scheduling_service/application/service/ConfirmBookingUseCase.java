package com.pm.scheduling_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pm.scheduling_service.application.inbox.ProcessedEvent;
import com.pm.scheduling_service.application.inbox.ProcessedEventRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConfirmBookingUseCase {
    private final SlotApplicationService slotApplicationService;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public void execute(UUID slotId, UUID patientId, UUID eventId) {
        // Idempotent check: skip if already processed (race condition guard)
        if (processedEventRepository.existsById(eventId)) {
            return;
        }

        slotApplicationService.confirmBooking(slotId, patientId);

        processedEventRepository.save(new ProcessedEvent(eventId));
    }
}
