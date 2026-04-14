package com.pm.scheduling_service.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pm.scheduling_service.domain.model.Slot;
import com.pm.scheduling_service.domain.port.SlotRepository;

import jakarta.persistence.EntityNotFoundException;

@Component
public class SlotTransactionalOperations {
    private final SlotRepository slotRepository;

    public SlotTransactionalOperations(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Transactional
    public Slot reserve(UUID slotId, UUID patientId) {
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.reserve(patientId, LocalDateTime.now());
        return slotRepository.save(slot);
    }

    @Transactional
    public void cancelReservation(UUID slotId, UUID patientId) {
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.cancelReservation(patientId);
    }

    @Transactional
    public void confirmBooking(UUID slotId, UUID patientId) {
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.confirmBooking(LocalDateTime.now(), patientId);
    }
}
