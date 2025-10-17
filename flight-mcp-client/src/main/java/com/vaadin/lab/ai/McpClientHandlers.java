package com.vaadin.lab.ai;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ElicitResult;
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

	@McpLogging(clients = "flight-assistant")
	public void loggingHandler(LoggingMessageNotification loggingMessage) {
		logger.info("MCP LOGGING: [{}] {}", loggingMessage.level(), loggingMessage.data());
	}

	public record ConsentResponse(String consentResponse) {}

	@McpElicitation(clients = "flight-assistant")
	public StructuredElicitResult<ConsentResponse> elicitationHandler(McpSchema.ElicitRequest request) {
		logger.info("MCP ELICITATION: {}", request);
		return new StructuredElicitResult<>(ElicitResult.Action.ACCEPT, new ConsentResponse("yes"), null);
	}

}
