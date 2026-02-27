package com.pm.scheduling_service.domain.port;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pm.scheduling_service.domain.model.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, UUID> {
    @Query("""
                SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Slot s WHERE s.doctorId = :doctorId AND
                NOT (:startTime > s.endTime AND :endTime > s.endTime OR :startTime < s.startTime AND :endTime < s.startTime)
            """)
    boolean hasTimeRangeConflictSlots(@Param("doctorId") UUID doctorId, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
