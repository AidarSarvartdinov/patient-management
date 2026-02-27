package com.pm.scheduling_service.api.dto;

import java.util.UUID;

public record ReserveSlotRequest(
    UUID patientId,
    UUID slotId
) {
    
}
