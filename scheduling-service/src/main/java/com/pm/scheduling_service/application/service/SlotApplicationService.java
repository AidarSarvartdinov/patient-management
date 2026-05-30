package com.pm.scheduling_service.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pm.scheduling_service.domain.exception.ConflictSlotsException;
import com.pm.scheduling_service.domain.model.Slot;
import com.pm.scheduling_service.domain.port.SlotRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SlotApplicationService {
    private final SlotRepository slotRepository;

    public SlotApplicationService(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Transactional
    public Slot createSlot(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime, long price) {
        log.info("Creating new time slot for doctor: {}, startTime: {}, endTime: {}", doctorId, startTime, endTime);

        if (slotRepository.hasTimeRangeConflictSlots(doctorId, startTime, endTime)) {
            throw new ConflictSlotsException("Found conflicting time slot for doctor: {}, startTime: {}, endTime: {}"
                    .formatted(doctorId, startTime, endTime));
        }

        Slot slot = new Slot(doctorId, startTime, endTime, price);
        return slotRepository.save(slot);
    }

    @Transactional
    public Slot reserveSlot(UUID slotId, UUID patientId) {
        log.info("Reserving slot {} for patient: {}", slotId, patientId);
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.reserve(patientId, LocalDateTime.now());
        return slotRepository.save(slot);
    }

    @Transactional
    public void cancelReservation(UUID slotId, UUID patientId) {
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.cancelReservation(patientId);
        slotRepository.save(slot);
    }

    @Transactional
    public void confirmBooking(UUID slotId, UUID patientId) {
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.confirmBooking(LocalDateTime.now(), patientId);
        slotRepository.save(slot);
    }
}
