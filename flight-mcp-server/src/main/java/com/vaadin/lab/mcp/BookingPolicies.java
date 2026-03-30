package com.vaadin.lab.mcp;

import java.nio.charset.StandardCharsets;

import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class BookingPolicies {

	@Value("classpath:/policies/terms-of-service.txt") Resource termsOfService;
	
	@McpResource(uri = "policy://terms-of-service", name = "Funair Terms of Service", description = "Terms of service for Funair")
	public String tos() {
		try {
			return this.termsOfService.getContentAsString(StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load terms of service", e);
		}
	}

}
