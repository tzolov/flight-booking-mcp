package com.vaadin.lab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.vaadin.lab.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingNumberIgnoreCaseAndCustomerFirstNameIgnoreCaseAndCustomerLastNameIgnoreCase(
            String bookingNumber, String firstName, String lastName);

    List<Booking> findAllByOrderByDateAsc();
}
