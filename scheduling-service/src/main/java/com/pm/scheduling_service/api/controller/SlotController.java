package com.pm.scheduling_service.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.scheduling_service.api.dto.CreateSlotRequest;
import com.pm.scheduling_service.api.dto.ReserveSlotRequest;
import com.pm.scheduling_service.application.service.CreateSlotUseCase;
import com.pm.scheduling_service.application.service.ReserveSlotUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
@Tag(name = "Slot", description = "API for managing time slots")
public class SlotController {
    private final CreateSlotUseCase createSlotUseCase;
    private final ReserveSlotUseCase reserveSlotUseCase;

    @PostMapping
    @Operation(summary = "Creates a new time slot for doctor")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Void> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        createSlotUseCase.execute(request.doctorId(), request.startTime(), request.endTime(), request.price());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve a slot for patient")
    @RolesAllowed("PATIENT")
    public ResponseEntity<Void> reserveSlot(@RequestBody ReserveSlotRequest request) {
        reserveSlotUseCase.execute(request.slotId(), request.patientId());
        return ResponseEntity.ok().build();
    }
}
