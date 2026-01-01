package uk.ac.roehampton.ziparound.booking;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.awt.print.Book;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

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
    private final ArrayList<Bookable> bookableArrayList;
    // Array that stores all current users
    private final ArrayList<User> userArrayList;
    // Staff object for permissions
    private final Staff staff;

    /**
     * Private Constructor for BookingManager
     * @param user Staff object required for low level permissions
     */
    private BookingManager(User user) {
        this.bookableArrayList = new ArrayList<>();
        this.bookingArrayList = new ArrayList<>();
        this.userArrayList = new ArrayList<>();
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

    // Getter for bookable item List
    public ArrayList<Bookable> getBookableArrayList() {
        return bookableArrayList;
    }

    // Getter for user List
    public ArrayList<User> getUserArrayList() {
        return userArrayList;
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
                if (isValid(booking)) {
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
     * Adds a bookable item to bookableArrayList
     *
     * @param bookable Bookable item to be added.
     */
    public void addBookable(Bookable bookable) {
        bookableArrayList.add(bookable);
    }

    /**
     * Adds a user to bookableArrayList
     *
     * @param user Bookable item to be added.
     */
    public void addUser(User user) {
        userArrayList.add(user);
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

    /**
     * This function is able to determine is a booking can be added to the list of bookings by checking the object's
     * ID (Based on Bookable Interface).
     * @param newBooking Booking to be checked.
     * @return Boolean - Whether the booking does not overlap with another booking having the same object's id.
     */
    boolean isValid(Booking newBooking) {
        // For each booking in the list of bookings
        for (Booking currentBooking : bookingArrayList) {
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
