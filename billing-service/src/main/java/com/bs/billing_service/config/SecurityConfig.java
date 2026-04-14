package com.bs.billing_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.bs.billing_service.security.RoleJwtGrantedAuthoritesConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    @Value("${jwkSetUri}")
    private String jwkSetUri;

    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/stripe/webhook").authorizeHttpRequests(c -> c.anyRequest().permitAll());

        http.csrf(c -> c.disable());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {

        http.authorizeHttpRequests(c -> c.anyRequest().authenticated());

        http.csrf(c -> c.disable());

        http.oauth2ResourceServer(
                c -> c.jwt(jwt -> jwt.jwkSetUri(jwkSetUri).jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RoleJwtGrantedAuthoritesConverter());
        return converter;
    }
}
