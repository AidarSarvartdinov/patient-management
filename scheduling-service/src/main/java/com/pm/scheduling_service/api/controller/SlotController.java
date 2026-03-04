package com.pm.scheduling_service.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.scheduling_service.api.dto.CreateSlotRequest;
import com.pm.scheduling_service.api.dto.ReserveSlotRequest;
import com.pm.scheduling_service.domain.service.SlotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/slots")
@Tag(name = "Slot", description = "API for managing time slots")
public class SlotController {
    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    @Operation(summary = "Creates a new time slot for doctor")
    public ResponseEntity<Void> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        slotService.createSlot(request.doctorId(), request.startTime(), request.endTime(), request.price());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve a slot for patient")
    public ResponseEntity<String> reserveSlot(@RequestBody ReserveSlotRequest request) {
        String sessionUrl = slotService.reserveSlot(request.slotId(), request.patientId());
        return ResponseEntity.ok(sessionUrl);
    }
}
