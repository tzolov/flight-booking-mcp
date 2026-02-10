package com.vaadin.lab.ai;

import java.util.Map;

import io.modelcontextprotocol.common.McpTransportContext;

import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestAttributes;

public class VaadinThreadLocalThingy {
    private static final ThreadLocal<McpTransportContext> vaadinContext = new ThreadLocal<>();

    private static final String AUTHENTICATION_KEY = Authentication.class.getName();

    private static final String REQUEST_ATTRIBUTES_KEY = RequestAttributes.class.getName();

    public static void setContext(Authentication authentication, RequestAttributes requestAttributes) {
        vaadinContext.set(
                McpTransportContext.create(
                        Map.of(AUTHENTICATION_KEY, authentication, REQUEST_ATTRIBUTES_KEY, requestAttributes)
                )
        );
    }

    public static void clear() {
        vaadinContext.remove();
    }

    public static McpTransportContext getContext() {
        return vaadinContext.get();
    }
}
