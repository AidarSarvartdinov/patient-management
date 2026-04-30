package com.pm.scheduling_service.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pm.scheduling_service.domain.service.SlotService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateSlotUseCase {
    private final SlotService slotService;

    @Transactional
    public void execute(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime, long price) {
        slotService.createSlot(doctorId, startTime, endTime, price);
    }
}
