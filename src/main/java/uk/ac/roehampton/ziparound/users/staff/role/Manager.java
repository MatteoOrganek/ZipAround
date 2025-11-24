/**
 * Manager.java
 * The Manager class is able to access most of the functionality of the system,
 * but is not able to delete certain objects.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.users.staff.role;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public class Manager extends Staff {
    /**
     * Constructor for Manager
     *
     * @param userID     ID that identifies each user
     * @param foreName   First name
     * @param lastname   Last name
     * @param department Staff department
     */
    public Manager(Integer userID, String foreName, String lastname, String department) {
        super(userID, foreName, lastname, department, true);
    }

    // Set permissions

    @Override public Boolean canDeleteBookings()    { return true; }
    @Override public Boolean canModifyBookings()    { return true; }
    @Override public Boolean canAddBookings()       { return true; }
    @Override public Boolean canViewBookingInfo()   { return true; }
    @Override public Boolean canApproveBookings()   { return true; }

    @Override public Boolean canModifyVehicles()    { return true; }
    @Override public Boolean canAddVehicles()       { return true; }
    @Override public Boolean canViewVehicleInfo()   { return true; }

    @Override public Boolean canModifyUsers()       { return true; }
    @Override public Boolean canAddUsers()          { return true; }
    @Override public Boolean canViewUserInfo()      { return true; }

    @Override public Boolean canModifyEquipment()   { return true; }
    @Override public Boolean canAddEquipment()      { return true; }
    @Override public Boolean canViewEquipmentInfo() { return true; }

    @Override public Boolean canModifyMaintenance()       { return true; }
    @Override public Boolean canViewMaintenanceInfo()     { return true; }


}

