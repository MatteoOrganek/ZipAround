/**
 * Admin.java
 * The Admin class can create admin objects that have all the permissions set to true.
 * This gives the Admin access to all functionalities of the system.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.users.staff.roles;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public class Admin extends Staff {

    public Admin(Integer userID, String foreName, String lastName, String department) {
        this.userID = userID;
        this.active = true;
        this.foreName = foreName;
        this.lastname = lastName;
        this.department = department;
    }

    // Set permissions

    @Override public Boolean canDeleteBookings()    { return true; }
    @Override public Boolean canModifyBookings()    { return true; }
    @Override public Boolean canAddBookings()       { return true; }
    @Override public Boolean canViewBookingInfo()   { return true; }

    @Override public Boolean canDeleteVehicles()    { return true; }
    @Override public Boolean canModifyVehicles()    { return true; }
    @Override public Boolean canAddVehicles()       { return true; }
    @Override public Boolean canViewVehicleInfo()   { return true; }

    @Override public Boolean canDeleteUsers()       { return true; }
    @Override public Boolean canModifyUsers()       { return true; }
    @Override public Boolean canAddUsers()          { return true; }
    @Override public Boolean canViewUserInfo()      { return true; }

    @Override public Boolean canDeleteEquipment()   { return true; }
    @Override public Boolean canModifyEquipment()   { return true; }
    @Override public Boolean canAddEquipment()      { return true; }
    @Override public Boolean canViewEquipmentInfo() { return true; }

    @Override public Boolean canModifyStaff()       { return true; }
    @Override public Boolean canViewStaffInfo()     { return true; }

}
