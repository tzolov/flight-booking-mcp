package com.vaadin.lab.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A hack for "anonymous" users supported in oauth2 client.
 */
public class VaadinAnonymousAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityContextRepository securityContextRepository;

    public VaadinAnonymousAuthenticationFilter() {
        this(new HttpSessionSecurityContextRepository());
    }

    public VaadinAnonymousAuthenticationFilter(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        SecurityContext context = SecurityContextHolder.getContext();

        if (context.getAuthentication() == null) {
            var authentication = new VaadinAnonymousAuthenticationToken();
            context.setAuthentication(authentication);
            securityContextRepository.saveContext(context, request, response);
        }

        filterChain.doFilter(request, response);
    }

}
