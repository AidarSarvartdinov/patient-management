package com.pm.scheduling_service.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSlotRequest(
        @NotNull UUID doctorId,
        @NotNull @JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime startTime,
        @NotNull @JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime endTime,
        @NotNull @Positive long price) {
}
