package com.pm.scheduling_service.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pm.scheduling_service.domain.exception.ConflictSlotsException;
import com.pm.scheduling_service.domain.model.Slot;
import com.pm.scheduling_service.domain.port.PaymentGateway;
import com.pm.scheduling_service.domain.port.SlotRepository;
import com.pm.scheduling_service.infrastructure.persistence.SlotTransactionalOperations;

@ExtendWith(MockitoExtension.class)
public class SlotServiceImplTest {
    @Mock
    private SlotRepository slotRepository;

    @Mock
    private SlotTransactionalOperations slotTransactionalOperations;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private SlotServiceImpl slotService;

    @Test
    void shouldCreateAndSaveSlot_whenNoTimeConflicts() {
        UUID doctorId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        long price = 10;

        // No time conflicts
        when(slotRepository.hasTimeRangeConflictSlots(doctorId, startTime, endTime)).thenReturn(false);

        // return the same object
        when(slotRepository.save(any(Slot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Slot slot = slotService.createSlot(doctorId, startTime, endTime, price);

        assertNotNull(slot);
        assertEquals(doctorId, slot.getDoctorId());

        // check that save() called 1 time
        verify(slotRepository).save(any(Slot.class));
    }

    @Test
    void shouldThrowConflictException_whenTimeConflictsExist() {
        UUID doctorId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        long price = 10;

        // slots have time conflict
        when(slotRepository.hasTimeRangeConflictSlots(doctorId, startTime, endTime)).thenReturn(true);

        assertThrows(ConflictSlotsException.class,
                () -> slotService.createSlot(doctorId, startTime, endTime, price));

        // slot with conflicts never saved
        verify(slotRepository, never()).save(any(Slot.class));
    }

    @Test
    void shouldReserveSlotAndReturnPaymentUrl_whenSuccessful() {
        UUID slotId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        String expectedUrl = "https://stripe.com/pay/1";

        Slot fakeSlot = new Slot(
                UUID.randomUUID(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                10);

        when(slotTransactionalOperations.reserve(slotId, patientId)).thenReturn(fakeSlot);

        when(paymentGateway.initiatePayment(patientId, slotId, fakeSlot.getPrice(), fakeSlot.getCurrency()))
                .thenReturn(expectedUrl);

        String actualUrl = slotService.reserveSlot(slotId, patientId);

        assertEquals(expectedUrl, actualUrl);

        verify(slotTransactionalOperations, never()).cancelReservation(any(), any());
    }

    @Test
    void shouldNotReserve_whenPaymentInitiationFailed() {
        UUID slotId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        Slot fakeSlot = new Slot(
                UUID.randomUUID(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                10);

        when(slotTransactionalOperations.reserve(slotId, patientId)).thenReturn(fakeSlot);

        when(paymentGateway.initiatePayment(patientId, slotId, fakeSlot.getPrice(), fakeSlot.getCurrency()))
                .thenThrow(RuntimeException.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> slotService.reserveSlot(slotId, patientId));
        assertEquals("Payment failed", exception.getMessage());

        verify(slotTransactionalOperations).cancelReservation(slotId, patientId);
    }

}
