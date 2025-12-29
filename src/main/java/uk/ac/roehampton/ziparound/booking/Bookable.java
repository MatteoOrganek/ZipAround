package uk.ac.roehampton.ziparound.booking;

import uk.ac.roehampton.ziparound.users.staff.Staff;

/**
 * An interface that defines an object to be bookable.
 */
public interface Bookable {
    Integer getID(Staff staff);
    Boolean isAvailable(Staff staff);
    void printInfo(Staff staff);
}
