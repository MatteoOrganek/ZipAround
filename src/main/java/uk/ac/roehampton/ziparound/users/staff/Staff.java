package uk.ac.roehampton.ziparound.users.staff;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.User;

public class Staff extends User implements Permissions {

    private String department;
    private Boolean active;

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
}
