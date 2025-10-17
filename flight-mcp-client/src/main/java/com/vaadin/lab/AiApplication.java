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

	@Autowired
	private McpClientHandlers mcpClientHandlers;

	public static void main(String[] args) {
		SpringApplication.run(AiApplication.class, args);
	}
}
