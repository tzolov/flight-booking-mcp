/* 
* Copyright 2025 - 2025 the original author or authors.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* https://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.vaadin.lab.mcp;

import com.vaadin.lab.model.BookingDetails;
import com.vaadin.lab.services.FlightBookingService;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageResult;
import io.modelcontextprotocol.spec.McpSchema.ElicitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springaicommunity.mcp.context.McpSyncRequestContext;
import org.springaicommunity.mcp.context.StructuredElicitResult;

import org.springframework.stereotype.Service;

/**
 * @author Christian Tzolov
 */
@Service
public class BookingTools {

	private static final Logger logger = LoggerFactory.getLogger(BookingTools.class);
	
	private final FlightBookingService flightBookingService;

	public BookingTools(FlightBookingService flightBookingService) {
		this.flightBookingService = flightBookingService;
	}

	@McpTool(description = "Provides flight booking details")
	public BookingDetails getBookingDetails(
		McpSyncRequestContext ctx,
		@McpToolParam(description = "The booking number") String bookingNumber,
		@McpToolParam(description = "The customer's first name") String firstName,
		@McpToolParam(description = "The customer's last name") String lastName) {

		ctx.info(String.format("Fetching booking details for bookingNumber: %s, firstName: %s, lastName: %s", bookingNumber,
				firstName, lastName));

		return this.flightBookingService.getBookingDetails(bookingNumber, firstName, lastName);
	}

	@McpTool(description = "Use to change a flight booking")
	public void changeBooking(
		McpSyncRequestContext ctx,
		@McpToolParam(description = "The booking number") String bookingNumber,
		@McpToolParam(description = "The customer's first name") String firstName,
		@McpToolParam(description = "The customer's last name") String lastName,
		@McpToolParam(description = "The new date for the flight") String newDate,
		@McpToolParam(description = "The departure airport code, (e.g. JFK)") String from,
		@McpToolParam(description = "The destination airport code (e.g. SFO)") String to) {

		ctx.info(
				String.format("Changing flight booking for bookingNumber: %s, firstName: %s, lastName: %s, newDate: %s, from: %s, to: %s",
						bookingNumber, firstName, lastName, newDate, from, to));

		this.flightBookingService.changeBooking(bookingNumber, firstName, lastName, newDate, from, to);
	};

	@McpTool(description = "Use to cancel an existing booking")
	public void cancelBooking(
		McpSyncRequestContext ctx,
		@McpToolParam(description = "The booking number") String bookingNumber,
		@McpToolParam(description = "The customer's first name") String firstName,
		@McpToolParam(description = "The customer's last name") String lastName) {		

		ctx.info(String.format("Cancelling flight booking for bookingNumber: %s, firstName: %s, lastName: %s",
				bookingNumber, firstName, lastName));

		this.flightBookingService.cancelBooking(bookingNumber, firstName, lastName);
	}


	public record ConsentResponse(String consentResponse) {}

	@McpTool(description = "Elicit user consent for a given request like changing or canceling a booking")
	public String getUserConsent(
		McpSyncRequestContext ctx,
		@McpToolParam(description = "The consent request") String consentRequest) {


		if (ctx.elicitEnabled()) {
			StructuredElicitResult<ConsentResponse> elicitationResult = ctx.elicit(
				e -> e.message("Do you consent to: " + consentRequest + "? (yes/no)"), ConsentResponse.class);

			if (elicitationResult.action() == ElicitResult.Action.ACCEPT) {
				return elicitationResult.structuredContent().consentResponse();					
			}
		}

		return "No consent provided. Please ask without tool usage.";
	}

	public record Person(String name, Number age) {}

	@McpTool(description = "Test tool", name = "tool1", generateOutputSchema = true)
	public String toolLoggingSamplingElicitationProgress(McpSyncRequestContext ctx, @McpToolParam String input) {

		ctx.info("Tool Invoked"); // call client logging (info level)

		ctx.progress(p -> p.percentage(25).message("tool call start")); // call client progress

		ctx.ping(); // call client ping

		StructuredElicitResult<Person> elicitationResult = ctx.elicit(e -> e.message("Fill in"), Person.class);
			
			
		ctx.progress(p -> p.progress(0.50).total(1.0).message("elicitation completed"));
		
		CreateMessageResult samplingResponse = ctx.sample(s -> s
			.message("Test Sampling Message")
			.maxTokens(500)
			.modelPreferences(mp -> mp.modelHints("OpenAi","Ollama")
					.costPriority(1.0)
					.speedPriority(1.0)
					.intelligencePriority(1.0)));

		ctx.progress(p -> p.progress(1.0).total(1.0).message("sampling completed"));

		ctx.info("Tool2 Done");

		return "CALL RESPONSE: " + samplingResponse.toString() + ", " + elicitationResult.toString();
	}

}
