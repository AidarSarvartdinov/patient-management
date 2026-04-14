package com.pm.scheduling_service.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.scheduling_service.domain.enums.SlotStatus;

public class SlotTest {
    private Slot slot;

    @BeforeEach
    public void createSlot() {
        slot = new Slot(
                UUID.randomUUID(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1);
    }

    // ===========================
    // Constructor
    // ===========================

    @Test
    public void shouldThrowException_whenCreatingSlotWithNullDoctor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Slot(null, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 1));
    }

    @Test
    public void shouldThrowException_whenEndTimeIsBeforeStartTime() {
        assertThrows(IllegalArgumentException.class,
                () -> new Slot(UUID.randomUUID(), LocalDateTime.now().plusHours(1), LocalDateTime.now(), 1));
    }

    @Test
    public void shouldThrowException_whenTimeRangeLessThan5Minutes() {
        assertThrows(IllegalArgumentException.class,
                () -> new Slot(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(4), 1));
    }

    @Test
    public void shouldThrowException_whenPriceNotPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new Slot(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now().plusHours(1), 0));
    }

    // ===========================
    // reserve
    // ===========================

    @Test
    public void shouldReserveSlot_whenSlotIsFree() {
        UUID patientId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        slot.reserve(patientId, now);

        assertEquals(SlotStatus.RESERVED, slot.getSlotStatus());
        assertEquals(patientId, slot.getPatientId());
        assertEquals(now, slot.getReservedAt());
    }

    @Test
    public void shouldNotAllowReservation_whenSlotIsAlreadyReserved() {
        slot.reserve(UUID.randomUUID(), LocalDateTime.now());

        UUID patientId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalStateException.class, () -> slot.reserve(patientId, now));
    }

    @Test
    public void shouldNotAllowReservation_whenStartTimeIsInPast() {
        LocalDateTime timeAfterSlotStarts = LocalDateTime.now().plusDays(1);

        assertThrows(IllegalStateException.class, () -> slot.reserve(UUID.randomUUID(), timeAfterSlotStarts));
    }

    @Test
    public void shouldReserveSlot_whenReserveAtStartTime() {
        LocalDateTime startTime = slot.getStartTime();

        slot.reserve(UUID.randomUUID(), startTime);

        assertEquals(SlotStatus.RESERVED, slot.getSlotStatus());
        assertEquals(startTime, slot.getReservedAt());
    }

    // ===========================
    // confirmBooking
    // ===========================

    @Test
    public void shouldConfirmBooking_whenSlotIsReserved() {
        UUID patientId = UUID.randomUUID();
        slot.reserve(patientId, LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        slot.confirmBooking(now, patientId);

        assertEquals(SlotStatus.CONFIRMED, slot.getSlotStatus());
        assertEquals(now, slot.getBookedAt());
    }

    @Test
    public void shouldNotAllowConfirmation_whenSlotIsNotReserved() {
        assertThrows(IllegalStateException.class, () -> slot.confirmBooking(LocalDateTime.now(), UUID.randomUUID()));
    }

    // ===========================
    // cancelReservation
    // ===========================

    @Test
    public void shouldCancelReservation_whenSlotIsReserved() {
        UUID patientId = UUID.randomUUID();
        slot.reserve(patientId, LocalDateTime.now());

        slot.cancelReservation(patientId);

        assertEquals(SlotStatus.FREE, slot.getSlotStatus());
        assertNull(slot.getPatientId());
        assertNull(slot.getReservedAt());
    }

    @Test
    public void shouldNotAllowToCancelReservation_whenPatientIdIsInvalid() {
        slot.reserve(UUID.randomUUID(), LocalDateTime.now());

        assertThrows(IllegalStateException.class, () -> slot.cancelReservation(UUID.randomUUID()));
    }

    @Test
    public void shouldNotAllowToCancelReservation_whenSlotIsNotReserved() {
        assertThrows(IllegalStateException.class, () -> slot.cancelReservation(UUID.randomUUID()));
    }

    // ===========================
    // cancelAppointment
    // ===========================

    @Test
    public void shouldCancelAppointment_whenExactly24HoursBeforeStart() {
        LocalDateTime now = LocalDateTime.now();
        Slot testSlot = new Slot(
                UUID.randomUUID(),
                now.plusHours(24),
                LocalDateTime.now().plusHours(25),
                1);
        UUID patientId = UUID.randomUUID();
        UUID cancellationId = UUID.randomUUID();

        testSlot.reserve(patientId, now);
        testSlot.confirmBooking(now, patientId);

        testSlot.cancelAppointment(now, cancellationId);

        assertEquals(SlotStatus.CANCELLATION_PENDING, testSlot.getSlotStatus());
        assertEquals(cancellationId, testSlot.getCancellationId());
    }

    @Test
    public void shouldNotAllowToCancelAppointment_whenSlotNotConfirmed() {
        assertThrows(IllegalStateException.class,
                () -> slot.cancelAppointment(LocalDateTime.now().minusHours(25), UUID.randomUUID()));
    }

    @Test
    public void shouldNotAllowToCancelAppointment_whenLessThan24HoursRemain() {
        UUID patientId = UUID.randomUUID();
        UUID cancellationId = UUID.randomUUID();

        slot.reserve(patientId, LocalDateTime.now());
        slot.confirmBooking(LocalDateTime.now(), patientId);

        assertThrows(IllegalStateException.class,
                () -> slot.cancelAppointment(LocalDateTime.now(), cancellationId));
    }

    // ===========================
    // release
    // ===========================

    @Test
    public void shouldNotAllowToRelease_whenStatusIsNotCancellationPending() {
        assertThrows(IllegalStateException.class, () -> slot.release(UUID.randomUUID()));
    }

    @Test
    public void shouldNotAllowToRelease_whenInvalidCancellationId() {
        UUID patientId = UUID.randomUUID();
        UUID cancellationId = UUID.randomUUID();
        LocalDateTime startTime = slot.getStartTime();

        slot.reserve(patientId, startTime.minusDays(4));
        slot.confirmBooking(startTime.minusDays(3), patientId);
        slot.cancelAppointment(startTime.minusDays(2), cancellationId);

        assertThrows(IllegalStateException.class, () -> slot.release(UUID.randomUUID()));
    }

    // ===========================
    // Lifecycle
    // ===========================

    @Test
    public void shouldCompleteFullLifecycle_fromReservationToRelease() {
        UUID patientId = UUID.randomUUID();
        UUID cancellationId = UUID.randomUUID();
        LocalDateTime startTime = slot.getStartTime();

        // Reserve
        slot.reserve(patientId, startTime.minusDays(4));
        assertEquals(SlotStatus.RESERVED, slot.getSlotStatus());

        // Confirm
        slot.confirmBooking(startTime.minusDays(3), patientId);
        assertEquals(SlotStatus.CONFIRMED, slot.getSlotStatus());

        // Cancel Appointment
        slot.cancelAppointment(startTime.minusDays(2), cancellationId);
        assertEquals(SlotStatus.CANCELLATION_PENDING, slot.getSlotStatus());

        // Release
        slot.release(cancellationId);
        assertEquals(SlotStatus.FREE, slot.getSlotStatus());
        assertNull(slot.getPatientId());
        assertNull(slot.getBookedAt());
        assertNull(slot.getCancellationId());
    }
}
