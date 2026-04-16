package com.bs.billing_service.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "outbox_events")
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "topic")
    private String topic;

    @NotNull
    @Column(name = "message_key")
    private String key;

    @NotNull
    @Column(name = "payload")
    private byte[] payload;

    protected Outbox() {
    }

    public Outbox(String topic, String key, byte[] payload) {
        this.topic = topic;
        this.key = key;
        this.payload = payload;
    }

    public UUID getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getKey() {
        return key;
    }

    public byte[] getPayload() {
        return payload;
    }

}
