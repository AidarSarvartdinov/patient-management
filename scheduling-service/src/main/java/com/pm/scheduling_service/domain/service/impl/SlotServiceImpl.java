package com.pm.scheduling_service.domain.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.scheduling_service.domain.exception.ConflictSlotsException;
import com.pm.scheduling_service.domain.model.Slot;
import com.pm.scheduling_service.domain.port.SlotRepository;
import com.pm.scheduling_service.domain.service.SlotService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SlotServiceImpl implements SlotService {
    private final SlotRepository slotRepository;

    public SlotServiceImpl(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Override
    public Slot createSlot(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime, long price) {
        log.info("Creating new time slot for doctor: {}, startTime: {}, endTime: {}", doctorId, startTime, endTime);
        if (slotRepository.hasTimeRangeConflictSlots(doctorId, startTime, endTime)) {
            throw new ConflictSlotsException("Found conflicting time slot for doctor: {}, startTime: {}, endTime: {}"
                    .formatted(doctorId, startTime, endTime));
        }

        Slot slot = new Slot(doctorId, startTime, endTime, price);
        return slotRepository.save(slot);
    }

    @Override
    public Slot reserveSlot(UUID slotId, UUID patientId) {
        log.info("Reserving slot {} for patient: {}", slotId, patientId);
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.reserve(patientId, LocalDateTime.now());
        return slotRepository.save(slot);
    }

    @Override
    public void cancelReservation(UUID slotId, UUID patientId) {
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.cancelReservation(patientId);
        slotRepository.save(slot);
    }

    @Override
    public void confirmBooking(UUID slotId, UUID patientId) {
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.confirmBooking(LocalDateTime.now(), patientId);
        slotRepository.save(slot);
    }
}
