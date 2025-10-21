package uk.ac.roehampton.ziparound.users.staff.roles;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public class Manager extends Staff {

    @Override public Boolean canDeleteBookings()    { return true; }
    @Override public Boolean canModifyBookings()    { return true; }
    @Override public Boolean canAddBookings()       { return true; }
    @Override public Boolean canViewBookingInfo()   { return true; }

    @Override public Boolean canModifyVehicles()    { return true; }
    @Override public Boolean canAddVehicles()       { return true; }
    @Override public Boolean canViewVehicleInfo()   { return true; }

    @Override public Boolean canModifyUsers()       { return true; }
    @Override public Boolean canAddUsers()          { return true; }
    @Override public Boolean canViewUserInfo()      { return true; }

    @Override public Boolean canModifyEquipment()   { return true; }
    @Override public Boolean canAddEquipment()      { return true; }
    @Override public Boolean canViewEquipmentInfo() { return true; }


}

