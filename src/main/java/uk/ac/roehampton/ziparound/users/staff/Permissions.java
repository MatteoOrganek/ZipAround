package uk.ac.roehampton.ziparound.users.staff;

public interface Permissions {

    // Booking Permissions
    default Boolean canDeleteBookings()    { return false; }
    default Boolean canModifyBookings()    { return false; }
    default Boolean canAddBookings()       { return false; }
    default Boolean canViewBookingInfo()   { return false; }

    // Vehicle Permissions
    default Boolean canDeleteVehicles()    { return false; }
    default Boolean canModifyVehicles()    { return false; }
    default Boolean canAddVehicles()       { return false; }
    default Boolean canViewVehicleInfo()   { return false; }

    // User Permissions
    default Boolean canDeleteUsers()       { return false; }
    default Boolean canModifyUsers()       { return false; }
    default Boolean canAddUsers()          { return false; }
    default Boolean canViewUserInfo()      { return false; }

    // Equipment Permissions
    default Boolean canDeleteEquipment()   { return false; }
    default Boolean canModifyEquipment()   { return false; }
    default Boolean canAddEquipment()      { return false; }
    default Boolean canViewEquipmentInfo() { return false; }

}
