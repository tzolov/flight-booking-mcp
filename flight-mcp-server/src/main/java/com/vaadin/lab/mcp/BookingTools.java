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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springaicommunity.mcp.context.McpSyncRequestContext;

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

		ctx.info(String.format("Changing flight booking for number: %s, first: %s, last: %s, newDate: %s, from: %s, to: %s",
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


	// Special Tool used to elicit user consent for booking change or cancellation operations
	// Uses the ConsentResponse record defined below to capture structured user consent response.
	
	public record ConsentResponse(String consent) {}

	@McpTool(description = "Elicit user consent for a given request like changing or canceling a booking")
	public String getUserConsent(McpSyncRequestContext ctx,
		@McpToolParam(description = "The consent request") String consentRequest) {

		ctx.info("Eliciting user consent for request: " + consentRequest);

		if (!ctx.elicitEnabled()) {
			logger.warn("Elicitation is not enabled in the current context. Cannot get user consent.");
			return "Elicitation not enabled. Please ask without tool usage.";
		}

		var elicitationResult = ctx.elicit(e -> e.message("Do you consent to: " + consentRequest), ConsentResponse.class);

		switch (elicitationResult.action()) {
			case ACCEPT:
				logger.info("User provided consent: " + elicitationResult.structuredContent().consent());
				return elicitationResult.structuredContent().consent();
			case DECLINE:
				logger.info("User rejected consent.");
				return "User didn't agree with the consent request! The answer is NO.";
			case CANCEL:
				logger.info("Elicitation was cancelled by the user.");
			default:
				return "No consent provided. Please ask without tool usage.";
		}
	}
}
