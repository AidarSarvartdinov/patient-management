package com.pm.auth_service.dto;

public record ErrorResponse(
    int code,
    String message
) {
    
}
