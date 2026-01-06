package uk.ac.roehampton.ziparound.equipment.maintenance;

import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.time.Instant;


/**
 * Interface that defines an object to be Maintainable.
 */
public interface Maintainable {
    /**
     * Book inspection will try to create a booking for this object and set SelfService as the client
     * @param startTime Start time of the booking
     * @param endTime Start time of the booking
     */
    void bookInspection(Instant startTime, Instant endTime);
    /**
     * Setter for last inspection booking
     * @param amountOfBookings Amount of bookings from last inspection
     * @param staff Staff for permissions
     */
    void setAmountOfBookings(Integer amountOfBookings, Staff staff);
    /**
     * Getter for last inspection booking
     * @param staff Staff for permissions
     */
    Integer getAmountOfBookings(Staff staff);
    /**
     * Getter for availability.
     * @param staff Staff for permissions
     */
    Boolean isAvailable(Staff staff);
}
