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

/**
 * The User class is an abstract class that is able to give basic attributes. Super class of Customer and Staff.
 */
public abstract class User {
    protected Integer userID;
    protected String foreName;
    protected String lastname;

    /**
     * Constructor for User
     * @param userID ID used to identify each user
     * @param foreName First name
     * @param lastname Last Name
     */
    public User(Integer userID, String foreName, String lastname) {
        this.userID = userID;
        this.foreName = foreName;
        this.lastname = lastname;
    }

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

    /**
     * Prints the full information for the user object
     * @param staff Staff used for permission handling
     */
    public void printFullInformation(Staff staff) {
        if (staff.canViewUserInfo())
            try {
                System.out.printf("%s - %s%n", getID(staff), getFullName(staff));
            }
            catch (SecurityException e) {
                System.out.println(e.getMessage());
            }
        else {
            throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);
        }
    }
}
