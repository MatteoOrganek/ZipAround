package uk.ac.roehampton.ziparound.booking;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle;
import uk.ac.roehampton.ziparound.users.Customer;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.awt.print.Book;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

// BookingManager is a Singleton Factory builder for Booking.
// I have made this decision based on the fact that the booking class represents only a specific object, without
// keeping any track and reference on the objects created. Additionally, as a singleton, all bookings can be accessed
// from one instance to avoid duplicates.

/**
 * Booking manager keeps track on each booking and is able to check for overlapping bookings or assign IDs.
 */
public class BookingManager {

    private static BookingManager instance;
    // Array that stores all current bookings
    private final ArrayList<Booking> bookingArrayList;
    // Array that stores all current bookable items
    private final ArrayList<Equipment> equipmentArrayList;
    private final ArrayList<Vehicle> vehicleArrayList;
    // Array that stores all current users
    private final ArrayList<Customer> customerArrayList;
    private final ArrayList<Staff> staffArrayList;
    // Staff object for permissions
    private Staff staff;

    /**
     * Private Constructor for BookingManager
     * @param user Staff object required for low level permissions
     */
    private BookingManager(User user) {
        this.equipmentArrayList = new ArrayList<>();
        this.vehicleArrayList = new ArrayList<>();
        this.bookingArrayList = new ArrayList<>();
        this.customerArrayList = new ArrayList<>();
        this.staffArrayList = new ArrayList<>();
        // Check if user is Staff
        if (user instanceof Staff) {
            // Assign user (staff) to this.staff
            this.staff = (Staff) user;
        }
        else {
            // User is likely a Customer, create a Self Service system that will act on behalf of the Staff.
            this.staff = new SelfService();
        }
    }

    /**
     * Get the singleton instance. Must provide User the first time.
     */
    public static synchronized BookingManager getInstance(User user) {
        // If instance does not exist
        if (instance == null) {
            // Call private constructor
            instance = new BookingManager(user);
        }
        return instance;
    }

    /**
     * Base constructor
     * @return Instance of BookingManager
     */
    public static synchronized BookingManager getInstance() {
        // If there is no instance
        if (instance == null) {
            // Throw error
            throw new IllegalStateException(
                    "BookingManager not initialized yet. Call getInstance(User) first."
            );
        }
        return instance;
    }

    // Getter for booking List
    public ArrayList<Booking> getBookingArrayList() {
        return bookingArrayList;
    }

    // Getter for vehicle item List
    public ArrayList<Vehicle> getVehicleArrayList() {
        return vehicleArrayList;
    }

    // Getter for equipment List
    public ArrayList<Equipment> getEquipmentArrayList() {
        return equipmentArrayList;
    }

    // Getter for customer item List
    public ArrayList<Customer> getCustomerArrayList() {
        return customerArrayList;
    }

    // Getter for user List
    public ArrayList<Staff> getStaffArrayList() {
        return staffArrayList;
    }


    /**
     * Creates and adds a booking to bookingArrayList based on whether the booking is valid and if it overlaps with
     * another booking containing the same object.
     *
     * @param bookedStartTime Start time of the booking.
     * @param bookedEndTime End time of the booking.
     * @param user User object.
     * @param bookableObject Bookable Object, including Vehicle and Equipment types.
     * @throws BookingException Will throw OverlappingBookingException and BookableObjectUnavailableException.
     */
    public void addBooking(Instant bookedStartTime,
                           Instant bookedEndTime,
                           User user,
                           Bookable bookableObject) throws BookingException {

        // If the bookable object is available
        if (bookableObject.isAvailable(staff)) {

            // Build booking
            Booking booking = new Booking(assignID(), bookedStartTime, bookedEndTime, Instant.now(),
                    user, bookableObject, false, null);

            // If the list is not null and not empty
            if (bookingArrayList != null && !bookingArrayList.isEmpty()) {
                // If the booking does not overlap
                if (isOverlapping(booking)) {
                    // Add the booking in the list.
                    bookingArrayList.add(booking);
                } else {
                    // Booking overlaps with another booking of the same item
                    throw new OverlappingBookingException();
                }
            } else {
                // Add Booking in list if the list is not null
                assert bookingArrayList != null;
                bookingArrayList.add(booking);
            }
        } else {
            // Bookable Object is not available
            throw new BookableObjectUnavailableException();
        }

    }

    /**
     * This function adds a booking to bookingArrayList
     * @param booking Booking to be added
     */
    public void addBooking(Booking booking) {
        bookingArrayList.add(booking);
    }

    /**
     * Adds a vehicle item to vehicleArrayList
     *
     * @param vehicle Vehicle item to be added.
     */
    public void addVehicle(Vehicle vehicle) {
        vehicleArrayList.add(vehicle);
    }

    /**
     * Adds a equipment item to equipmentArrayList
     *
     * @param equipment Equipment item to be added.
     */
    public void addEquipment(Equipment equipment) {
        equipmentArrayList.add(equipment);
    }

    /**
     * Adds a customer to customerArrayList
     *
     * @param customer Customer item to be added.
     */
    public void addCustomer(Customer customer) {
        customerArrayList.add(customer);
    }

    /**
     * Adds a staff to staffArrayList
     *
     * @param staff Staff item to be added.
     */
    public void addStaff(Staff staff) {
        staffArrayList.add(staff);
    }

    /**
     * This function removes a specific booking (even duplicates) in the booking list.
     * @param booking Booking to be removed.
     * @param staff Staff for permissions.
     */
    public void removeBooking(Booking booking, Staff staff) {
        for (Booking currentBooking : bookingArrayList) {
            if (Objects.equals(currentBooking.getID(staff), booking.getID(staff))) {
                bookingArrayList.remove(booking);
            }
        }
    }

    public Boolean isTheTimeInOrder(Booking booking) {
        // Check if the current booking's start and end time have been swapped
        if (booking.getBookedStartTime(staff).isAfter(booking.getBookedEndTime(staff))) {
            return false;
        }
        return true;
    }

    /**
     * This function is able to determine is a booking can be added to the list of bookings by checking the object's
     * ID (Based on Bookable Interface).
     * @param newBooking Booking to be checked.
     * @return Boolean - Whether the booking does not overlap with another booking having the same object's id.
     */
    public boolean isOverlapping(Booking newBooking) {

        // For each booking in the list of bookings
        for (Booking currentBooking : bookingArrayList) {
            // Check if the booking's id does not match, as it should not count (assuming that it is trying to modify the booking's info)
            if (newBooking.getID(staff) != currentBooking.getID(staff)) {
                // If the bookable object's ids match
                if (Objects.equals(currentBooking.bookableObject.getID(staff), newBooking.bookableObject.getID(staff))) {

                    // Define start and end time of the current and new booking
                    Instant currentBookingStartTime = currentBooking.getBookedStartTime(staff);
                    Instant newBookingStartTime = newBooking.getBookedStartTime(staff);
                    Instant currentBookingEndTime = currentBooking.getBookedEndTime(staff);
                    Instant newBookingEndTime = newBooking.getBookedEndTime(staff);

                    // (StartNew < EndCurrent)  and  (EndNew > StartCurrent) = overlap, hence, return false, as it cannot be booked
                    boolean c1 = newBookingStartTime.isBefore(currentBookingEndTime);
                    boolean c2 = newBookingEndTime.isAfter(currentBookingStartTime);

                    if (c1 && c2) {
                        return false;
                    }
                }
            }
        }
        // All checks are false, return true as the booking does not overlap any other bookings
        return true;
    }

    /**
     * This function assigns a new ID based on the last item's id on the booking list.
     * @return New ID.
     */
    // TODO Remove this as it may cause some issues later on
    int assignID() {
        // If the booking list is not empty
        if (!bookingArrayList.isEmpty()){
            // Get last element index of the list.
            int lastItemIndex = bookingArrayList.toArray().length - 1;
            // Return the last booking's id and add one.
            return bookingArrayList.get(lastItemIndex).getID(staff) + 1;
        }
        else {
            // The list is empty, so there is no bookings, hence return 0.
            return 0;
        }
    }

    /**
     * This function gives the last created booking in the booking list.
     * @return Last Booking to be created, will return null if no items are in booking list.
     */
    public Booking getLastBooking() {
        if (staff.canViewBookingInfo()) {
            return bookingArrayList.get(bookingArrayList.toArray().length - 1);
        }
        else {
            throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);
        }
    }

    public void approveLastBooking() {
        if (staff.canApproveBookings()) {
            getLastBooking().setApproved(true, staff);
            getLastBooking().setStaff(staff, staff);
        }
        else {
            throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION);
        }

    }

    public void resetBookingArrayList() {
        this.bookingArrayList.clear();
    }
    public void resetVehicleArrayList() {
        this.vehicleArrayList.clear();
    }
    public void resetEquipmentArrayList() {
        this.equipmentArrayList.clear();
    }
    public void resetCustomerArrayList() {
        this.customerArrayList.clear();
    }
    public void resetStaffArrayList() {
        this.staffArrayList.clear();
    }

    public LocalDateTime getStartLocalDateTime(Booking booking) {
        return LocalDateTime.ofInstant(booking.getBookedStartTime(staff), ZoneId.systemDefault());
    }

    public LocalDateTime getEndLocalDateTime(Booking booking) {
        return LocalDateTime.ofInstant(booking.getBookedEndTime(staff), ZoneId.systemDefault());
    }

    // Booking exceptions

    public static class BookingException extends Exception {
        public BookingException(String message) {
            super(message);
        }
    }

    public static class BookableObjectUnavailableException extends BookingException {
        public BookableObjectUnavailableException() {
            super("The bookable object is not available to book.");
        }
    }

    public static class OverlappingBookingException extends BookingException {
        public OverlappingBookingException() {
            super("Two bookings with the same bookable object cannot overlap.");
        }
    }

    public static class BookingNotFoundException extends BookingException {
        public BookingNotFoundException() {
            super("The requested booking was not found.");
        }
    }

}
