package uk.ac.roehampton.ziparound.users.staff.roles;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public class BookingAgent extends Staff {

    @Override public Boolean canModifyBookings()    { return true; }
    @Override public Boolean canAddBookings()       { return true; }
    @Override public Boolean canViewBookingInfo()   { return true; }

    @Override public Boolean canViewVehicleInfo()   { return true; }

    @Override public Boolean canAddUsers()          { return true; }

    @Override public Boolean canViewEquipmentInfo() { return true; }

}
