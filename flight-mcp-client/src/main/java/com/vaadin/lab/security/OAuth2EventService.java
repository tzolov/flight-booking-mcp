package com.vaadin.lab.security;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class OAuth2EventService {

    private final Map<String, Consumer<TokenObtainedEvent>> listeners = new ConcurrentHashMap<>();
    private final Consumer<TokenObtainedEvent> NOOP = evt -> {
    };

    @EventListener
    void onAuthSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof OAuth2AuthorizationCodeAuthenticationToken) {
            listeners.getOrDefault(SecurityContextHolder.getContext().getAuthentication().getName(), NOOP)
                    .accept(new TokenObtainedEvent());
        }
    }

    public void addListener(String userId, Consumer<TokenObtainedEvent> callback) {
        listeners.putIfAbsent(userId, callback);
    }

    public void removeListener(String userId) {
        listeners.remove(userId);
    }

    public record TokenObtainedEvent() {
    }
}
