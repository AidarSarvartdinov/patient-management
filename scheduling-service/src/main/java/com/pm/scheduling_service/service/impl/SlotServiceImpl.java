package com.pm.scheduling_service.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.scheduling_service.exception.ConflictSlotsException;
import com.pm.scheduling_service.model.Slot;
import com.pm.scheduling_service.repository.SlotRepository;
import com.pm.scheduling_service.service.SlotService;

@Service
public class SlotServiceImpl implements SlotService {
    private final SlotRepository slotRepository;

    public SlotServiceImpl(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public Slot createSlot(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        if (slotRepository.hasTimeRangeConflictSlots(doctorId, startTime, endTime)) {
            throw new ConflictSlotsException("You have conflicting time slots");
        }


        Slot slot = new Slot(doctorId, startTime, endTime);
        return slotRepository.save(slot);
    }
}
