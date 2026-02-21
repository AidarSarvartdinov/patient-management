package com.pm.scheduling_service.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pm.scheduling_service.enums.SlotStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "doctor_id")
    private UUID doctorId;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private SlotStatus slotStatus = SlotStatus.FREE;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "created_at")
    private LocalDateTime bookedAt;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

    @Column(name = "cancellation_id")
    private UUID cancellationId;

    @Version
    private long version;

    public Slot(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        if (doctorId == null) {
            throw new IllegalArgumentException("doctorId must not be null");
        }
        if (!(startTime.isBefore(endTime))) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }

        this.doctorId = doctorId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void confirmBooking(LocalDateTime now) {
        if (startTime.isBefore(now)) {
            throw new IllegalStateException("Cannot book past slot");
        }

        if (!slotStatus.equals(SlotStatus.RESERVED)) {
            throw new IllegalStateException(
                    "Cannot book slot with appointment status: " + slotStatus.toString());
        }

        if (patientId == null) {
            throw new IllegalStateException("Cannot book slot without patient");
        }

        this.slotStatus = SlotStatus.CONFIRMED;
        this.bookedAt = LocalDateTime.now();
    }

    public void reserve(UUID patientId, LocalDateTime now) {

        if (startTime.isBefore(now)) {
            throw new IllegalStateException("Cannot reserve past slot");
        }

        if (!slotStatus.equals(SlotStatus.FREE)) {
            throw new IllegalStateException("Slot already has appointment");
        }

        this.slotStatus = SlotStatus.RESERVED;
        this.patientId = patientId;
        this.reservedAt = now;
    }

    public void release(UUID cancellationId) {

        if (!slotStatus.equals(SlotStatus.CANCELLATION_PENDING)) {
            throw new IllegalStateException("Slot is not awaiting cancellation");
        }

        if (this.cancellationId == null || !cancellationId.equals(this.cancellationId)) {
            throw new IllegalStateException("Invalid cancellation ID");
        }

        this.slotStatus = SlotStatus.FREE;
        this.patientId = null;
        this.cancellationId = null;
        this.bookedAt = null;
        this.reservedAt = null;

    }

    public void cancelAppointment(LocalDateTime now) {
        if (!slotStatus.equals(SlotStatus.CONFIRMED)) {
            throw new IllegalStateException(
                    "Cannot cancel appointment with status: " + slotStatus.toString());
        }

        if (now.plusHours(24).isAfter(this.startTime)) {
            throw new IllegalStateException("Appointment cancellation must be 24 hours before the start");
        }

        this.slotStatus = SlotStatus.CANCELLATION_PENDING;
        this.cancellationId = UUID.randomUUID();
    }

    public void cancelReservation(UUID patientId) {
        if (!slotStatus.equals(SlotStatus.RESERVED)) {
            throw new IllegalStateException(
                    "Cannot cancel reservation with appointment status: " + slotStatus.toString());
        }

        if (this.patientId == null || !this.patientId.equals(patientId)) {
            throw new IllegalStateException("Cannot cancel reservation");
        }

        this.slotStatus = SlotStatus.FREE;
        this.patientId = null;
        this.reservedAt = null;
    }
}
