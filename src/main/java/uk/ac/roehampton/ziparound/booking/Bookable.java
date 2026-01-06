/**
 * Bookable.java
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 *
 * @see uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle
 * @see uk.ac.roehampton.ziparound.equipment.Equipment
 */

package uk.ac.roehampton.ziparound.booking;

import uk.ac.roehampton.ziparound.users.staff.Staff;

/**
 * An interface that defines an object to be bookable.
 */
public interface Bookable {
    Integer getID(Staff staff);
    String getName(Staff staff);
    String getModel(Staff staff);
    Integer getAmountOfBookings(Staff staff);
    Boolean isAvailable(Staff staff);
    void setAvailable(Boolean available, Staff staff);
    void printInfo(Staff staff);
}
