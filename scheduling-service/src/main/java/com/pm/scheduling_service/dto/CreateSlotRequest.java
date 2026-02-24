package com.pm.scheduling_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

public record CreateSlotRequest(
                @NotNull UUID doctorId,
                @NotNull @JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime startTime,
                @NotNull @JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime endTime) {
}
