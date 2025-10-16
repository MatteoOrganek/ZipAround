package uk.ac.roehampton.ziparound.users.staff;

public interface Permissions {
    default Boolean canModifyBookings() { return false; };
    default Boolean canModifyVehicles() { return false; };
    default Boolean canModifyUsers()    { return false; };
}
