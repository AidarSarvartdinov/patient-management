package com.pm.scheduling_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.scheduling_service.dto.CreateSlotRequest;
import com.pm.scheduling_service.service.SlotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/slots")
public class SlotController {
    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    public ResponseEntity<Void> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        slotService.createSlot(request.doctorId(), request.startTime(), request.endTime());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
