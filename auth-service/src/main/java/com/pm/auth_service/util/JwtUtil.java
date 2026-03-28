package com.pm.auth_service.util;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.stereotype.Component;


@Component
public class JwtUtil {

    private final JwtEncoder jwtEncoder;
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(Authentication auth) throws JwtEncodingException {
        String email = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        Instant now = Instant.now();

        log.info("Generating token for user with email: " + email + " and role: " + role);

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(email)
                .claim("role", role)
                .issuer("http://auth-service:4005")
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofMinutes(15)))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
