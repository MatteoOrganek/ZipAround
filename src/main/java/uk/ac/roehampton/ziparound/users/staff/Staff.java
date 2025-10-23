/**
 * Staff.java
 * SuperClass of Admin, Manager and BookingAgent.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 *
 * @see uk.ac.roehampton.ziparound.users.staff.Staff
 */

package uk.ac.roehampton.ziparound.users.staff;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.User;

public abstract class Staff extends User implements Permissions {

    protected String department;
    protected Boolean active;

    public String getDepartment(Staff staff) {
        if (staff.canViewStaffInfo()) { return department;}
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setDepartment(String department, Staff staff) {
        if (staff.canModifyStaff()) { this.department = department;}
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    public Boolean isActive(Staff staff) {
        if (staff.canViewStaffInfo()) { return active;}
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setActive(Boolean active, Staff staff) {
        if (staff.canModifyStaff()) { this.active = active;}
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    public String getPermissionSummary(Staff staff) {
        if (staff.canViewStaffInfo()) {
            return "Permissions:\n" + "\tBookings: " +
                    (canViewBookingInfo() ? "View " : "") +
                    (canAddBookings() ? "Add " : "") +
                    (canModifyBookings() ? "Modify " : "") +
                    (canDeleteBookings() ? "Delete " : "") +
                    "\n\tVehicles: " +
                    (canViewVehicleInfo() ? "View " : "") +
                    (canAddVehicles() ? "Add " : "") +
                    (canModifyVehicles() ? "Modify " : "") +
                    (canDeleteVehicles() ? "Delete " : "") +
                    "\n\tUsers: " +
                    (canViewUserInfo() ? "View " : "") +
                    (canAddUsers() ? "Add " : "") +
                    (canModifyUsers() ? "Modify " : "") +
                    (canDeleteUsers() ? "Delete " : "") +
                    "\n\tEquipment: " +
                    (canViewEquipmentInfo() ? "View " : "") +
                    (canAddEquipment() ? "Add " : "") +
                    (canModifyEquipment() ? "Modify " : "") +
                    (canDeleteEquipment() ? "Delete " : "");
        }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    @Override public void printFullInformation(Staff staff) {
        try {
            System.out.printf("[%s] [%s - %s] - %s\n%s%n", isActive(staff) ? "Active" : "Not Active", getID(staff), getDepartment(staff), getFullName(staff), getPermissionSummary(staff));
        }
        catch (SecurityException e) {
            System.out.println(e.getMessage());
        }
    }
}
