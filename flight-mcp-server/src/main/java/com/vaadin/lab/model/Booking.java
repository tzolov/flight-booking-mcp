package com.vaadin.lab.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bookingNumber;

	@Column(name = "flight_date")
	private LocalDate date;

	private LocalDate bookingTo;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(name = "origin")
	private String from;

	@Column(name = "destination")
	private String to;

	@Enumerated(EnumType.STRING)
	private BookingStatus bookingStatus;

	private String seatNumber;

	@Enumerated(EnumType.STRING)
	private BookingClass bookingClass;

	public Booking() {
	}

	public Booking(String bookingNumber, LocalDate date, Customer customer, BookingStatus bookingStatus, String from,
			String to, String seatNumber, BookingClass bookingClass) {
		this.bookingNumber = bookingNumber;
		this.date = date;
		this.customer = customer;
		this.bookingStatus = bookingStatus;
		this.from = from;
		this.to = to;
        this.seatNumber = seatNumber;
		this.bookingClass = bookingClass;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBookingNumber() {
		return bookingNumber;
	}

	public void setBookingNumber(String bookingNumber) {
		this.bookingNumber = bookingNumber;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDate getBookingTo() {
		return bookingTo;
	}

	public void setBookingTo(LocalDate bookingTo) {
		this.bookingTo = bookingTo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public BookingClass getBookingClass() {
		return bookingClass;
	}

	public void setBookingClass(BookingClass bookingClass) {
		this.bookingClass = bookingClass;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

}