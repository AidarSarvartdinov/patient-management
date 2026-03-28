package com.pm.scheduling_service.infrastructure.security;

import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class RoleJwtGrantedAuthoritesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public List<GrantedAuthority> convert(Jwt jwt) {
        String role = jwt.getClaimAsString("role");

        if (role == null) {
            return List.of();
        }

        return List.of(new SimpleGrantedAuthority(role));
    }
    
}
