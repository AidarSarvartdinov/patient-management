package com.pm.auth_service.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.dto.LoginResponseDTO;
import com.pm.auth_service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "API for login and register")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Validated @RequestBody LoginRequestDTO loginRequestDTO) {
        String token = authService.authenticate(loginRequestDTO);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Register a new user and generate token")
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Validated @RequestBody LoginRequestDTO loginRequestDTO) {
        String token = authService.register(loginRequestDTO, "PATIENT");
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDTO(token));
    }
}
