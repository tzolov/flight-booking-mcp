package com.vaadin.lab.services;

import java.util.List;

import com.vaadin.lab.model.BookingDetails;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    private final FlightBookingService flightBookingService;

    public BookingController(FlightBookingService flightBookingService) {
        this.flightBookingService = flightBookingService;
    }

    @GetMapping("/bookings")
    public List<BookingDetails> bookings() {
        return flightBookingService.getBookings();
    }
    
}
