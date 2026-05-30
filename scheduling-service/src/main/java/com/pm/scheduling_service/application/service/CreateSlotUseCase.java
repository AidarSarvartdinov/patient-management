package com.pm.scheduling_service.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateSlotUseCase {
    private final SlotApplicationService slotApplicationService;

    @Transactional
    public void execute(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime, long price) {
        slotApplicationService.createSlot(doctorId, startTime, endTime, price);
    }
}
