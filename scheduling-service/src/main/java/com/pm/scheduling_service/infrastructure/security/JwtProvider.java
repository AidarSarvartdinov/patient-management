package com.pm.scheduling_service.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import com.pm.scheduling_service.infrastructure.exception.UnauthorizedException;

@Component
public class JwtProvider {
    public String getTokenString() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }

        throw new UnauthorizedException("Jwt not found in SecurityContext");
    }
}
