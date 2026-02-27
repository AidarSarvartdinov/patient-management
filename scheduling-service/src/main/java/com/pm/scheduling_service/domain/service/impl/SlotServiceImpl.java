package com.pm.scheduling_service.domain.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.pm.scheduling_service.domain.exception.ConflictSlotsException;
import com.pm.scheduling_service.domain.model.Slot;
import com.pm.scheduling_service.domain.port.SlotRepository;
import com.pm.scheduling_service.domain.service.SlotService;
import com.pm.scheduling_service.infrastructure.client.dto.CreatePaymentRequest;
import com.pm.scheduling_service.infrastructure.client.dto.CreatePaymentResponse;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SlotServiceImpl implements SlotService {
    private final SlotRepository slotRepository;
    private final RestClient restClient;

    public SlotServiceImpl(SlotRepository slotRepository, RestClient.Builder restClientBuilder) {
        this.slotRepository = slotRepository;
        this.restClient = restClientBuilder.baseUrl("http://localhost:4004").build();
    }

    public Slot createSlot(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime, long price) {
        if (slotRepository.hasTimeRangeConflictSlots(doctorId, startTime, endTime)) {
            throw new ConflictSlotsException("You have conflicting time slots");
        }

        Slot slot = new Slot(doctorId, startTime, endTime, price);
        return slotRepository.save(slot);
    }

    @Transactional
    public String reserveSlot(UUID slotId, UUID patientId) {
        Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.reserve(patientId, LocalDateTime.now());
        slotRepository.save(slot);
        ResponseEntity<CreatePaymentResponse> responseEntity = restClient.post()
                .uri("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .body(new CreatePaymentRequest(patientId, slotId, slot.getPrice(), slot.getCurrency())).retrieve()
                .toEntity(CreatePaymentResponse.class);

        // TODO: retry if bad response
        CreatePaymentResponse response = responseEntity.getBody();
        return response.sessionUrl();
    }
}
