package com.vaadin.lab.services;

import com.vaadin.lab.model.Booking;
import com.vaadin.lab.model.BookingDetails;
import com.vaadin.lab.model.BookingStatus;
import com.vaadin.lab.repository.BookingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FlightBookingService {

	private final BookingRepository bookingRepository;

	public FlightBookingService(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	// -----------------------------
	// Booking Service
	// -----------------------------
	public List<BookingDetails> getBookings() {
		return bookingRepository.findAllByOrderByDateAsc().stream().map(this::toBookingDetails).toList();
	}

	private Booking findBooking(String bookingNumber, String firstName, String lastName) {
		return bookingRepository
				.findByBookingNumberIgnoreCaseAndCustomerFirstNameIgnoreCaseAndCustomerLastNameIgnoreCase(
						bookingNumber, firstName, lastName)
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));
	}

	public BookingDetails getBookingDetails(String bookingNumber, String firstName, String lastName) {
		var booking = findBooking(bookingNumber, firstName, lastName);
		return toBookingDetails(booking);
	}

	public void changeBooking(String bookingNumber, String firstName, String lastName, String newDate, String from,
			String to) {
		var booking = findBooking(bookingNumber, firstName, lastName);
		if (booking.getDate().isBefore(LocalDate.now().plusDays(1))) {
			throw new IllegalArgumentException("Booking cannot be changed within 24 hours of the start date.");
		}
		booking.setDate(LocalDate.parse(newDate));
		booking.setFrom(from);
		booking.setTo(to);
		bookingRepository.save(booking);
	}

	public void cancelBooking(String bookingNumber, String firstName, String lastName) {
		var booking = findBooking(bookingNumber, firstName, lastName);
		if (booking.getDate().isBefore(LocalDate.now().plusDays(2))) {
			throw new IllegalArgumentException("Booking cannot be cancelled within 48 hours of the start date.");
		}
		booking.setBookingStatus(BookingStatus.CANCELLED);
		bookingRepository.save(booking);
	}

	private BookingDetails toBookingDetails(Booking booking) {
		return new BookingDetails(booking.getBookingNumber(), booking.getCustomer().getFirstName(),
				booking.getCustomer().getLastName(), booking.getDate(), booking.getBookingStatus(), booking.getFrom(),
				booking.getTo(), booking.getSeatNumber(), booking.getBookingClass().toString());
	}

	public void changeSeat(String bookingNumber, String firstName, String lastName, String seatNumber) {
		var booking = findBooking(bookingNumber, firstName, lastName);
		booking.setSeatNumber(seatNumber);
		bookingRepository.save(booking);
	}

}
