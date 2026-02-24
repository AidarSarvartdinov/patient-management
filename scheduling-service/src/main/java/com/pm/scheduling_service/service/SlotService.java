package com.pm.scheduling_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pm.scheduling_service.model.Slot;

public interface SlotService {
    Slot createSlot(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime);
}
