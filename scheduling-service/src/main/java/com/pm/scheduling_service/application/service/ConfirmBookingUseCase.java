package com.pm.scheduling_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pm.scheduling_service.domain.service.SlotService;
import com.pm.scheduling_service.infrastructure.messaging.inbox.Inbox;
import com.pm.scheduling_service.infrastructure.messaging.inbox.InboxRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConfirmBookingUseCase {
    private final SlotService slotService;
    private final InboxRepository inboxRepository;

    @Transactional
    public void execute(UUID slotId, UUID patientId, UUID eventId) {
        // Idempotent check: skip if already processed (race condition guard)
        if (inboxRepository.existsById(eventId)) {
            return;
        }

        slotService.confirmBooking(slotId, patientId);

        inboxRepository.save(new Inbox(eventId));
    }
}
