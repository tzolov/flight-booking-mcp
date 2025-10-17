package com.vaadin.lab.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.LocalDate;

// @ formatter:off
@JsonInclude(Include.NON_NULL)
public record BookingDetails(
	String bookingNumber, 
	String firstName, 
	String lastName, 
	LocalDate date,
	BookingStatus bookingStatus, 
	String from, 
	String to, 
	String seatNumber, 
	String bookingClass) {
}
// @ formatter:on