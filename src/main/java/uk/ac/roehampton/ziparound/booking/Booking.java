package uk.ac.roehampton.ziparound.booking;

import org.jetbrains.annotations.NotNull;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.vehicles.Vehicle;

import java.time.Instant;
import java.util.ArrayList;

public class Booking {

    // Booking info
    Instant bookedStartTime;
    Instant bookedEndTime;
    User user;

    ArrayList<Equipment> lEquipment;
    ArrayList<Vehicle> lVehicles;

    Boolean approved;
    Staff staff;

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

    // Getter / Setter for "user"
    public User getUser(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return user; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setUser(@NotNull User user, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.user = user; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "lEquipment"
    public ArrayList<Equipment> getLEquipment(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return lEquipment; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setLEquipment(@NotNull ArrayList<Equipment> lEquipment, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.lEquipment = lEquipment; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "lVehicles"
    public ArrayList<Vehicle> getLVehicles(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return lVehicles; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setLVehicles(@NotNull ArrayList<Vehicle> lVehicles, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.lVehicles = lVehicles; }
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
        if (staff.canViewBookingInfo()) { return staff; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setStaff(@NotNull Staff newStaff, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.staff = newStaff; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }
}
