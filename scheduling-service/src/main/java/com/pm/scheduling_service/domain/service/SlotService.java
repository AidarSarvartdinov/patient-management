package com.pm.scheduling_service.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pm.scheduling_service.domain.model.Slot;

public interface SlotService {
    Slot createSlot(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime, long price);
    Slot reserveSlot(UUID slotId, UUID patientId);
    void cancelReservation(UUID slotId, UUID patientId);
    void confirmBooking(UUID slotId, UUID patientId);
}
