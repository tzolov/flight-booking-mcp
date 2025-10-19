package com.vaadin.lab;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.lab.ai.McpClientHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Push
@SpringBootApplication
public class AiApplication implements AppShellConfigurator {

	// Workaround until https://github.com/spring-projects/spring-ai/pull/4671
	@Autowired
	private McpClientHandlers mcpClientHandlers; 

	public static void main(String[] args) {
		SpringApplication.run(AiApplication.class, args);
	}
}
