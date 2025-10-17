package com.vaadin.lab.repository;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import com.vaadin.lab.model.Booking;
import com.vaadin.lab.model.BookingClass;
import com.vaadin.lab.model.BookingStatus;
import com.vaadin.lab.model.Customer;

@Component
public class DataInitializer implements ApplicationRunner {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public DataInitializer(CustomerRepository customerRepository, BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Clear existing data
        bookingRepository.deleteAll();
        customerRepository.deleteAll();

        // Create 10 realistic bookings covering all test cases from terms-of-service.txt

        // 1. Economy booking - too close to change (within 24 hours - today's date)
        Customer customer1 = new Customer("Emma", "Wilson");
        customerRepository.save(customer1);
        Booking booking1 = new Booking("B1001", LocalDate.now(), customer1,
                BookingStatus.CONFIRMED, "JFK", "LAX", "12A", BookingClass.ECONOMY);
        bookingRepository.save(booking1);

        // 2. Economy booking - too close to cancel (within 48 hours but after 24 hours)
        Customer customer2 = new Customer("James", "Anderson");
        customerRepository.save(customer2);
        Booking booking2 = new Booking("B1002", LocalDate.now().plusDays(1), customer2,
                BookingStatus.CONFIRMED, "SFO", "LHR", "15C", BookingClass.ECONOMY);
        bookingRepository.save(booking2);

        // 3. Economy booking - can be changed and cancelled
        Customer customer3 = new Customer("Olivia", "Martinez");
        customerRepository.save(customer3);
        Booking booking3 = new Booking("B1003", LocalDate.now().plusDays(5), customer3,
                BookingStatus.CONFIRMED, "CDG", "FRA", "8B", BookingClass.ECONOMY);
        bookingRepository.save(booking3);

        // 4. Premium Economy booking - too close to change (today)
        Customer customer4 = new Customer("Noah", "Thompson");
        customerRepository.save(customer4);
        Booking booking4 = new Booking("B1004", LocalDate.now(), customer4,
                BookingStatus.CONFIRMED, "ARN", "HEL", "5D", BookingClass.PREMIUM_ECONOMY);
        bookingRepository.save(booking4);

        // 5. Premium Economy booking - can be changed but with fee
        Customer customer5 = new Customer("Sophia", "Garcia");
        customerRepository.save(customer5);
        Booking booking5 = new Booking("B1005", LocalDate.now().plusDays(7), customer5,
                BookingStatus.CONFIRMED, "MUC", "MAD", "3A", BookingClass.PREMIUM_ECONOMY);
        bookingRepository.save(booking5);

        // 6. Business Class booking - too close to cancel (within 48 hours)
        Customer customer6 = new Customer("Liam", "Rodriguez");
        customerRepository.save(customer6);
        Booking booking6 = new Booking("B1006", LocalDate.now().plusDays(1), customer6,
                BookingStatus.CONFIRMED, "LAX", "JFK", "2C", BookingClass.BUSINESS);
        bookingRepository.save(booking6);

        // 7. Business Class booking - can be changed (free changes)
        Customer customer7 = new Customer("Isabella", "Lee");
        customerRepository.save(customer7);
        Booking booking7 = new Booking("B1007", LocalDate.now().plusDays(10), customer7,
                BookingStatus.CONFIRMED, "LHR", "CDG", "1A", BookingClass.BUSINESS);
        bookingRepository.save(booking7);

        // 8. Already cancelled Economy booking
        Customer customer8 = new Customer("Ethan", "Walker");
        customerRepository.save(customer8);
        Booking booking8 = new Booking("B1008", LocalDate.now().plusDays(14), customer8,
                BookingStatus.CANCELLED, "FRA", "ARN", "18F", BookingClass.ECONOMY);
        bookingRepository.save(booking8);

        // 9. Long-distance international Business Class
        Customer customer9 = new Customer("Ava", "Hall");
        customerRepository.save(customer9);
        Booking booking9 = new Booking("B1009", LocalDate.now().plusDays(21), customer9,
                BookingStatus.CONFIRMED, "SFO", "HEL", "4B", BookingClass.BUSINESS);
        bookingRepository.save(booking9);

        // 10. Far future Premium Economy booking
        Customer customer10 = new Customer("Mason", "Young");
        customerRepository.save(customer10);
        Booking booking10 = new Booking("B1010", LocalDate.now().plusDays(30), customer10,
                BookingStatus.CONFIRMED, "MAD", "SFO", "11E", BookingClass.PREMIUM_ECONOMY);
        bookingRepository.save(booking10);

        System.out.println("Database initialized with 10 realistic flight bookings");
    }
}
