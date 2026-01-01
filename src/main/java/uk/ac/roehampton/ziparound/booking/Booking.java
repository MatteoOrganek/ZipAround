/**
 * Booking.java
 * Booking skeleton.
 * Handles booking information and provides methods for management.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.booking;

import org.jetbrains.annotations.NotNull;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.Customer;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;

import java.time.Instant;

public class Booking {

    /** Identification for each booking */
    public Integer bookingID;
    /** Start time */
    Instant bookedStartTime;
    /** End time */
    Instant bookedEndTime;
    /** Time of creation */
    Instant createdOn;
    /** User */
    User user;
    /** Bookable object, can be either Vehicle or Equipment */
    Bookable bookableObject;
    /** Approved By staff */
    Boolean approved;
    /** Staff that approved the booking */
    Staff staffApproved;

    /**
     * Constructor for Booking
     * @param bookingID ID used to identify each booking
     * @param bookedStartTime Start time for the booking
     * @param bookedEndTime End time for the booking
     * @param createdOn Creation timestamp
     * @param user User object
     * @param bookableObject Bookable Object, including Vehicle and Equipment types.
     * @param approved Whether the booking has been approved by a staff
     * @param staffApproved Staff that approved the booking
     */
    public Booking(Integer bookingID,
                   Instant bookedStartTime,
                   Instant bookedEndTime,
                   Instant createdOn,
                   User user,
                   Bookable bookableObject,
                   Boolean approved,
                   Staff staffApproved) {
        this.bookingID = bookingID;
        this.bookedStartTime = bookedStartTime;
        this.bookedEndTime = bookedEndTime;
        this.createdOn = createdOn;
        this.user = user;
        this.bookableObject = bookableObject;
        this.approved = approved;
        this.staffApproved = staffApproved;
    }

    // Getter / Setter for "bookingID"
    public Integer getID(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return bookingID; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }
    
    // Getter / Setter for "bookedStartTime" - Can be null
    public Instant getBookedStartTime(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return bookedStartTime; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setBookedStartTime(Instant bookedStartTime, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.bookedStartTime = bookedStartTime; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "bookedEndTime" - Can be null
    public Instant getBookedEndTime(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return bookedEndTime; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setBookedEndTime(Instant bookedEndTime, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.bookedEndTime = bookedEndTime; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "createdOn" - Can be null
    public Instant getCreatedOn(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return createdOn; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setCreatedOn(Instant createdOn, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.createdOn = createdOn; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "user"
    public User getUser(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return user; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setUser(@NotNull User user, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.user = user; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "bookableObject"
    public Bookable getBookableObject(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return bookableObject; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }
    public void setBookableObject(@NotNull Bookable bookableObject, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.bookableObject = bookableObject; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "approved"
    public Boolean getApproved(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return approved; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setApproved(@NotNull Boolean approved, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.approved = approved; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "staff"
    public Staff getStaff(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return this.staffApproved; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setStaff(Staff staffApproved, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.staffApproved = staffApproved; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    /**
     * Prints a comprehensive list of all the attributes in the current booking.
     *
     * @param staff SStaff for permissions
     */
    public void printInfo(@NotNull Staff staff) {
        if (!staff.canViewBookingInfo()) {
            throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);
        }

        System.out.println("===== Booking Details ==============================================");
        System.out.println("Booked Start Time : " + bookedStartTime);
        System.out.println("Booked End Time   : " + bookedEndTime);
        System.out.println("Created On        : " + createdOn);

        if (user != null) {
            System.out.println("Customer Name     : " + user.getFullName(staff));
            System.out.println("Customer ID       : " + user.getID(staff));
        } else {
            System.out.println("Customer          : null");
        }

        System.out.println("Approved          : " + approved);
        System.out.println("Staff Responsible : " + (this.staffApproved != null ? this.staffApproved.getFullName(staff) : "null"));

        // If bookable object is not null, print
        if (bookableObject != null) {
            bookableObject.printInfo(staff);
        } else {
            System.out.println("[None]");
        }
        System.out.println("====================================================================");
    }

}
