package com.vaadin.lab.ai;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpElicitation;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.context.StructuredElicitResult;

import org.springframework.stereotype.Service;

@Service
public class McpClientHandlers {

	private static final Logger logger = LoggerFactory.getLogger(McpClientHandlers.class);

	private final ElicitationService elicitationService;

	public McpClientHandlers(ElicitationService elicitationService) {
		this.elicitationService = elicitationService;
	}

	@McpLogging(clients = "flight-assistant")
	public void loggingHandler(LoggingMessageNotification loggingMessage) {
		logger.info("MCP LOGGING: [{}] {}", loggingMessage.level(), loggingMessage.data());
	}

	public record ConsentResponse(String consent) {}

	@McpElicitation(clients = "flight-assistant")
	public StructuredElicitResult<ConsentResponse> elicitationHandler(McpSchema.ElicitRequest request) {
		logger.info("MCP ELICITATION: {}", request);
		
		// Request user consent via the UI
		ElicitationService.ElicitationResponse response = elicitationService.requestUserConsent(request);
		
		return new StructuredElicitResult<>(
			response.action(), 
			new ConsentResponse(response.message()), 
			null
		);
	}

}
