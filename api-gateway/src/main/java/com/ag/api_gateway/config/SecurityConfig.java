package com.ag.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {
    @Value("${jwkSetUri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http
        .csrf(c -> c.disable())
        .authorizeExchange(exchanges -> exchanges.pathMatchers("/auth/**").permitAll()
                .anyExchange().authenticated())
                .oauth2ResourceServer(c -> c.jwt(
                        jwt -> jwt.jwkSetUri(jwkSetUri)));
        return http.build();
    }
}
