package com.pm.scheduling_service.infrastructure.messaging.inbox;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<Inbox, UUID> {
    
}
