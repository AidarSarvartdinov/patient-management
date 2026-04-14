package com.pm.scheduling_service.domain.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.scheduling_service.domain.exception.ConflictSlotsException;
import com.pm.scheduling_service.domain.model.Slot;
import com.pm.scheduling_service.domain.port.PaymentGateway;
import com.pm.scheduling_service.domain.port.SlotRepository;
import com.pm.scheduling_service.domain.service.SlotService;
import com.pm.scheduling_service.infrastructure.persistence.SlotTransactionalOperations;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SlotServiceImpl implements SlotService {
    private final SlotRepository slotRepository;
    private final SlotTransactionalOperations slotTransactionalOperations;
    private final PaymentGateway paymentGateway;

    public SlotServiceImpl(SlotRepository slotRepository, PaymentGateway paymentGateway,
            SlotTransactionalOperations slotTransactionalOperations) {
        this.slotRepository = slotRepository;
        this.slotTransactionalOperations = slotTransactionalOperations;
        this.paymentGateway = paymentGateway;
    }

    public Slot createSlot(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime, long price) {
        log.info("Creating new time slot for doctor: {}, startTime: {}, endTime: {}", doctorId, startTime, endTime);
        if (slotRepository.hasTimeRangeConflictSlots(doctorId, startTime, endTime)) {
            throw new ConflictSlotsException("Found conflicting time slot for doctor: {}, startTime: {}, endTime: {}"
                    .formatted(doctorId, startTime, endTime));
        }

        Slot slot = new Slot(doctorId, startTime, endTime, price);
        return slotRepository.save(slot);
    }

    public String reserveSlot(UUID slotId, UUID patientId) {
        log.info("Reserving slot {} for patient: {}", slotId, patientId);
        Slot slot = slotTransactionalOperations.reserve(slotId, patientId);
        try {
            String sessionUrl = paymentGateway.initiatePayment(patientId, slotId, slot.getPrice(), slot.getCurrency());
            return sessionUrl;
        } catch (Exception e) {
            slotTransactionalOperations.cancelReservation(slotId, patientId);

            // processing in the GlobalExceptionHandler
            throw e;
        }
    }

    public void confirmSlotByPayment(UUID slotId, UUID patientId) {
        log.info("Confirming booking slot {} for paitent {}", slotId, patientId);
        // TODO: handle transaction failure
        slotTransactionalOperations.confirmBooking(slotId, patientId);
    }
}
