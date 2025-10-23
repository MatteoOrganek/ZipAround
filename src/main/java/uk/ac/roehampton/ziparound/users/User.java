/**
 * User.java
 * Represents a general user within the booking system.
 * Handles user information and provides methods for management.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.users;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.staff.Staff;

public abstract class User {
    protected Integer userID;
    protected String foreName;
    protected String lastname;

    public Integer getID(Staff staff) {
        if (staff.canViewUserInfo()) { return userID; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public String getForeName(Staff staff) {
        if (staff.canViewUserInfo()) { return foreName; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setForeName(String forename, Staff staff) {
        if (staff.canModifyUsers()) { this.foreName = forename; }
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
        if (staff.canViewUserInfo()) { return "%s %s".formatted(foreName, lastname); }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void printFullInformation(Staff staff) {
        try {
            System.out.printf("%s - %s%n", getID(staff), getFullName(staff));
        }
        catch (SecurityException e) {
            System.out.println(e.getMessage());
        }
    }
}
