package uk.ac.roehampton.ziparound.users;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.staff.Staff;

public class User {
    protected Integer id;
    protected String forename;
    protected String lastname;

    public Integer getId(Staff staff) {
        if (staff.canViewUserInfo()) { return id; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public String getForeName(Staff staff) {
        if (staff.canViewUserInfo()) { return forename; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setForeName(String forename, Staff staff) {
        if (staff.canModifyUsers()) { this.forename = forename; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    public String getLastName(Staff staff) {
        if (staff.canViewUserInfo()) { return lastname; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setLastName(String lastname, Staff staff) {
        if (staff.canModifyUsers()) { this.lastname = lastname; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    public String getFullName(Staff staff) {
        if (staff.canViewUserInfo()) { return "%s %s".formatted(forename, lastname); }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }
}
