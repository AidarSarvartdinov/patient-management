package com.ag.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
public class SecurityConfig {
    @Value("${jwkSetUri}")
    private String jwkSetUri;

    @Bean
    @Order(1)
    public SecurityWebFilterChain publicWebFilterChain(ServerHttpSecurity http) {
        http
            .securityMatcher(new OrServerWebExchangeMatcher(
                ServerWebExchangeMatchers.pathMatchers("/auth/**"),
                ServerWebExchangeMatchers.pathMatchers("/stripe/webhook")
            ))
            .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain protectedWebFilterChain(ServerHttpSecurity http) {
        http
            .securityMatcher(new NegatedServerWebExchangeMatcher(
                new OrServerWebExchangeMatcher(
                    ServerWebExchangeMatchers.pathMatchers("/auth/**"),
                    ServerWebExchangeMatchers.pathMatchers("/stripe/webhook")
                )
            ))
            .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
            .csrf(csrf -> csrf.disable())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwkSetUri(jwkSetUri)));
        return http.build();
    }
}
