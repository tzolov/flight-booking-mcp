package com.vaadin.lab.security;

import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.AbstractAuthenticationToken;


/**
 * A hack for "anonymous" users supported in oauth2 client.
 */
public class VaadinAnonymousAuthenticationToken extends AbstractAuthenticationToken {

    private final String name;

    public VaadinAnonymousAuthenticationToken() {
        super(Collections.emptyList());
        this.name = UUID.randomUUID().toString();
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
