package com.pm.scheduling_service.infrastructure.messaging.inbox;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, UUID> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Inbox i WHERE i.createdAt < :retentionDate")
    int deleteByCreatedAtBefore(@Param("retentionDate") LocalDateTime retentionDate);
}
