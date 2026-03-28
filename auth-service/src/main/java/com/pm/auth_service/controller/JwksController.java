package com.pm.auth_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@RestController
public class JwksController {
    private final Map<String, Object> jwkSetJson;

    public JwksController(JWKSource<SecurityContext> jwkSource) throws KeySourceException {
        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().build());
        JWKSet jwkSet = new JWKSet(jwkSource.get(jwkSelector, null)).toPublicJWKSet();
        this.jwkSetJson = jwkSet.toJSONObject();
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> jwks() {
        return ResponseEntity.ok(jwkSetJson);
    }
}
