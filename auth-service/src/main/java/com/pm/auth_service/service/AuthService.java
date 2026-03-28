package com.pm.auth_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.util.JwtUtil;

@Service
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public String authenticate(LoginRequestDTO loginRequestDTO) {
        log.info("Authenticating user with email: " + loginRequestDTO.getEmail());
        
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));

        
        String token = jwtUtil.generateToken(auth);

        return token;
    }

    public String register(LoginRequestDTO loginRequestDTO, String role) {
        userService.createUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword(), role);

        String token = authenticate(loginRequestDTO);

        return token;
    }
}
