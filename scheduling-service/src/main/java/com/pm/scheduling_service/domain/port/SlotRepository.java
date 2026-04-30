package com.pm.scheduling_service.domain.port;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.pm.scheduling_service.domain.model.Slot;

public interface SlotRepository {
    Slot save(Slot slot);
    Optional<Slot> findById(UUID slotId);
    boolean hasTimeRangeConflictSlots(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime);
}
