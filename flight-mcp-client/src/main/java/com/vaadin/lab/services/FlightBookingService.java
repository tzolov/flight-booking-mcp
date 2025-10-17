package com.vaadin.lab.services;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FlightBookingService {

	private final RestClient restClient;

	public FlightBookingService() {
		this.restClient = RestClient.create("http://localhost:8081");
	}

	// -----------------------------
	// Booking Service
	// -----------------------------
	public List<BookingDetails> getBookings() {
		List<BookingDetails> bookings = restClient.get()
			.uri("/bookings")
			.retrieve()
			.body(new ParameterizedTypeReference<List<BookingDetails>>() {
			});
			
		return bookings;	
	}

}
