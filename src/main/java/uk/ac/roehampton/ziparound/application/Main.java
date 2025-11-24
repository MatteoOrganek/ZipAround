package uk.ac.roehampton.ziparound.application;

import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.users.Customer;
import uk.ac.roehampton.ziparound.users.staff.role.Admin;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.EBike;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.Scooter;

import java.time.Instant;

public class Main {
    public static void main(String[] args) throws BookingManager.BookingException {
        // Staff creation
        Admin admin1 = new Admin(
                1,
                "Matteo",
                "Organek",
                "IT");

        // User creation
        Customer customer1 = new Customer(
                1,
                "Luigi",
                "Di Tommaso");

        Customer customer2 = new Customer(
                2,
                "John",
                "Doe");

        // Vehicle declaration
        Scooter scooter1 = new Scooter(
                1,
                "DGI",
                "ES2341",
                12.3f,
                30,
                2);
        EBike eBike1 = new EBike(
                2,
                "VDS",
                "ES1037",
                45.2f,
                35,
                1);

        // Equipment declaration
        Equipment helmet1 = new Equipment(
                3,
                "Helmet",
                "A sturdy helmet. ",
                true);


        // Create Singleton BookingManager
        // As the User is a Customer, the system will default to SelfService, an extension of Staff.
        // Changing customer1 to admin1 will give more permissions.
        BookingManager bookingManager = BookingManager.getInstance(customer1);
        String timeString;

        // Define booking time
        timeString = "2025-11-21T22:15:30Z";
        Instant start = Instant.parse(timeString);
        timeString = "2025-11-22T22:15:30Z";
        Instant end = Instant.parse(timeString);

        // Add booking with a request from customer 1 to book scooter 1.
        bookingManager.addBooking(start, end, customer1, scooter1);
        bookingManager.getLastBooking().printInfo(admin1);
        // Add booking with a request from customer 1 to book ebike 1.
        bookingManager.addBooking(start, end, customer1, eBike1);
        bookingManager.getLastBooking().printInfo(admin1);
        // Try to approve the booking (if the user/staff does not have enough permissions, it will return an error.)
        System.out.print("\n\nTrying to Approve booking... ");
        try { bookingManager.approveLastBooking(); } catch (SecurityException e) { System.out.println(e.getMessage()); }
        System.out.println("\n");
        // Add booking with a request from customer 1 to book helmet 1 with the same time as the booking before.
        bookingManager.addBooking(start, end, customer1, helmet1);
        bookingManager.getLastBooking().printInfo(admin1);

        // Define new booking time
        timeString = "2025-11-22T22:15:30Z";
        start = Instant.parse(timeString);
        timeString = "2025-11-24T22:15:30Z";
        end = Instant.parse(timeString);

        // Add booking with a request from customer 2 to book scooter 1 with the same time as the booking before.
        bookingManager.addBooking(start, end, customer2, scooter1);
        bookingManager.getLastBooking().printInfo(admin1);
        // Try to book the same booking with customer1.
        try { bookingManager.addBooking(start, end, customer1, scooter1); }
        // It will throw OverlappingBookingException, catch it and explain that the booking is overlapping.
        catch (BookingManager.OverlappingBookingException e) { System.out.println("This item has already been booked for this time slot. Please retry."); }

    }
}

