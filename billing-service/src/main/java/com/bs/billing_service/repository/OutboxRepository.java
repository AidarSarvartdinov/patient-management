package com.bs.billing_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bs.billing_service.model.Outbox;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    
}
