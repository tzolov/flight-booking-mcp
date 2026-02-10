package com.vaadin.lab;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springaicommunity.mcp.security.server.config.McpServerOAuth2Configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                // Enforce authentication with token on EVERY request
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/bookings").permitAll();
                    auth.anyRequest().authenticated();
                })
                // Configure OAuth2 on the MCP server
                .with(
                        McpServerOAuth2Configurer.mcpServerOAuth2(),
                        (mcpAuthorization) -> {
                            mcpAuthorization.authorizationServer("http://localhost:9000");
                        }
                )
                .cors(cors -> cors.configurationSource(this::getCorsConfiguration))
                .csrf(CsrfConfigurer::disable)
                .build();
    }


    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        return configuration;
    }

}
