package com.vaadin.lab.security;

import java.util.Map;
import java.util.function.Consumer;

import com.vaadin.lab.ai.VaadinThreadLocalThingy;
import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import org.springaicommunity.mcp.security.client.sync.oauth2.http.client.OAuth2AuthorizationCodeSyncHttpRequestCustomizer;

import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .oauth2Client(Customizer.withDefaults())
                .csrf(CsrfConfigurer::disable)
                .addFilterBefore(new VaadinAnonymousAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    Consumer<DefaultOAuth2AuthorizedClientManager> noContextMapper() {
        return manager -> manager.setContextAttributesMapper(req -> Map.of());
    }

    @Bean
    McpSyncClientCustomizer syncClientCustomizer() {
        return (name, syncSpec) ->
                syncSpec.transportContextProvider(
                        VaadinThreadLocalThingy::getContext
                );
    }

    @Bean
    McpSyncHttpClientRequestCustomizer requestCustomizer(
            OAuth2AuthorizedClientManager clientManager
    ) {
        // The clientRegistration name, "authserver",
        // must match the name in application.properties
        return new OAuth2AuthorizationCodeSyncHttpRequestCustomizer(
                clientManager,
                "authserver"
        );
    }


}
