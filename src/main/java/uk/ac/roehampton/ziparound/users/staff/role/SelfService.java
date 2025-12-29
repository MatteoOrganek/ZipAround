package uk.ac.roehampton.ziparound.users.staff.role;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public class SelfService extends Staff {
    /**
     * Constructor for Self Service, a staff extension that grants the user basic booking privileges
     */
    public SelfService() {
        super(-1, "Self", "Service", "System", true);
    }

    // Set permissions

    @Override public Boolean canDeleteBookings()    { return true; }
    @Override public Boolean canModifyBookings()    { return true; }
    @Override public Boolean canAddBookings()       { return true; }
    @Override public Boolean canViewBookingInfo()   { return true; }

    @Override public Boolean canViewVehicleInfo()   { return true; }

    @Override public Boolean canViewUserInfo()      { return true; }

    @Override public Boolean canViewEquipmentInfo() { return true; }
}
