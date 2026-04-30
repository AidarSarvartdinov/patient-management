package com.pm.scheduling_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pm.scheduling_service.domain.service.SlotService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReserveSlotUseCase {
    private final SlotService slotService;

    @Transactional
    public void execute(UUID slotId, UUID patientId) {
        slotService.reserveSlot(slotId, patientId);
    }
}
