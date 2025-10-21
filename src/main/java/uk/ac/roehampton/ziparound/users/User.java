package uk.ac.roehampton.ziparound.users;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public class User {
    protected Integer id;
    protected String forename;
    protected String lastname;

    public String getForeName(Staff staff) {
        if (staff.canViewUserInfo()) { return forename; }
        else { return null; }
    }

    public int setForeName(String forename, Staff staff) {
        if (staff.canModifyUsers()) { this.forename = forename; return 1; }
        else { return 0; }
    }

    public String getLastName(Staff staff) {
        if (staff.canViewUserInfo()) { return lastname; }
        else { return null; }
    }

    public int setLastName(String lastname, Staff staff) {
        if (staff.canModifyUsers()) { this.lastname = lastname; return 1; }
        else { return 0; }
    }

    public String getFullName(Staff staff) {
        if (staff.canViewUserInfo()) { return "%s %s".formatted(forename, lastname); }
        else { return null; }
    }
}
