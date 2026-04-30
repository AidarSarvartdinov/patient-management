package com.pm.scheduling_service.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.pm.scheduling_service.domain.model.Slot;
import com.pm.scheduling_service.domain.port.SlotRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SlotRepositoryAdapter implements SlotRepository {
    private final JpaSlotRepository jpaSlotRepository;

    @Override
    public Slot save(Slot slot) {
        return jpaSlotRepository.save(slot);
    }

    @Override
    public Optional<Slot> findById(UUID slotId) {
        return jpaSlotRepository.findById(slotId);
    }

    @Override
    public boolean hasTimeRangeConflictSlots(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        return jpaSlotRepository.hasTimeRangeConflictSlots(doctorId, startTime, endTime);
    }
    
}
