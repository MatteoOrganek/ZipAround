/**
 * BookingAgent.java
 * The BookingAgent class can manage bookings
 * but is not able to modify anything else except adding users.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.users.staff.role;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public class BookingAgent extends Staff {
    public BookingAgent(Integer userID, String foreName, String lastname, String department) {
        super(userID, foreName, lastname, department, true);
    }

    // Set permissions

    @Override public Boolean canDeleteBookings()    { return true; }
    @Override public Boolean canModifyBookings()    { return true; }
    @Override public Boolean canAddBookings()       { return true; }
    @Override public Boolean canViewBookingInfo()   { return true; }
    @Override public Boolean canApproveBookings()   { return true; }

    @Override public Boolean canViewVehicleInfo()   { return true; }

    @Override public Boolean canAddUsers()          { return true; }
    @Override public Boolean canViewUserInfo()      { return true; }


    @Override public Boolean canViewEquipmentInfo() { return true; }

    @Override public Boolean canModifyMaintenance()       { return true; }
    @Override public Boolean canViewMaintenanceInfo()     { return true; }

}

