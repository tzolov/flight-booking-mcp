package com.vaadin.lab.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ElicitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class ElicitationService {

    private static final Logger logger = LoggerFactory.getLogger(ElicitationService.class);
    private final List<Consumer<ElicitationRequest>> listeners = new ArrayList<>();
    private final Object listenersLock = new Object();

    public record ElicitationRequest(
        McpSchema.ElicitRequest request,
        CompletableFuture<ElicitationResponse> responseFuture
    ) {}

    public record ElicitationResponse(
        ElicitResult.Action action,
        String message
    ) {}

    public void addListener(Consumer<ElicitationRequest> listener) {
        synchronized (listenersLock) {
            listeners.add(listener);
            logger.info("Listener added. Total listeners: {}", listeners.size());
        }
    }

    public void removeListener(Consumer<ElicitationRequest> listener) {
        synchronized (listenersLock) {
            listeners.remove(listener);
            logger.info("Listener removed. Total listeners: {}", listeners.size());
        }
    }

    public ElicitationResponse requestUserConsent(McpSchema.ElicitRequest request) {
        logger.info("Requesting user consent. Number of registered listeners: {}", listeners.size());
        
        CompletableFuture<ElicitationResponse> future = new CompletableFuture<>();
        ElicitationRequest elicitRequest = new ElicitationRequest(request, future);

        // Notify all listeners (UI components) asynchronously
        CompletableFuture.runAsync(() -> {
            List<Consumer<ElicitationRequest>> listenersCopy;
            synchronized (listenersLock) {
                listenersCopy = new ArrayList<>(listeners);
            }
            logger.info("Notifying {} listeners", listenersCopy.size());
            listenersCopy.forEach(listener -> {
                try {
                    logger.info("Notifying listener: {}", listener);
                    listener.accept(elicitRequest);
                } catch (Exception e) {
                    logger.error("Error notifying listener", e);
                }
            });
        });

        try {
            logger.info("Waiting for user response...");
            // Wait for user response (with timeout)
            ElicitationResponse response = future.get(60, TimeUnit.SECONDS);
            logger.info("Received user response: {}", response.action());
            return response;
        } catch (Exception e) {
            logger.error("Error waiting for user consent", e);
            // Default to reject on error/timeout
            return new ElicitationResponse(ElicitResult.Action.DECLINE, "Timeout or error occurred");
        }
    }

    public void respondToElicitation(CompletableFuture<ElicitationResponse> future, ElicitationResponse response) {
        future.complete(response);
    }
}
