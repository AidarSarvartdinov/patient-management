package com.pm.scheduling_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pm.scheduling_service.domain.model.Slot;
import com.pm.scheduling_service.domain.port.PaymentGateway;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReserveSlotUseCase {
    private final PaymentGateway paymentGateway;
    private final SlotApplicationService slotApplicationService;

    
    public String execute(UUID slotId, UUID patientId) {
        Slot slot = slotApplicationService.reserveSlot(slotId, patientId);
        
        try {
            return paymentGateway.initiatePayment(slot.getPatientId(), slot.getId(), slot.getPrice(), slot.getCurrency());
        } catch (Exception e) {
            slotApplicationService.cancelReservation(slotId, patientId);
            throw e;
        }
    }

}
