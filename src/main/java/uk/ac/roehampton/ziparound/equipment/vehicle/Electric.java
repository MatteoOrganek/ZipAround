/**
 * Electric.java
 * Represents the electric state of a Vehicle
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.equipment.vehicle;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.staff.Staff;

/**
 * An interface that defines an object to be electric.
 */
public interface Electric {

    default Boolean isElectric() { return true; }

    // Getter / Setter for "amountOfBatteries"
    default Integer getAmountOfBatteries(Staff staff) {
        if (staff.canViewVehicleInfo()) { return getAmountOfBatteries(); }
        else {throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);}}

    Integer getAmountOfBatteries();

    // Getter / Setter for "maxPowerKw"
    default Integer getMaxPowerKw(Staff staff) {
        if (staff.canViewVehicleInfo()) { return getMaxPowerKw(); }
        else {throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);}}

    Integer getMaxPowerKw();

    default void setMaxPowerKw(Integer maxPowerKw, Staff staff) {
        if (staff.canModifyVehicles()) { setMaxPowerKw(maxPowerKw);}
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    void setMaxPowerKw(Integer maxPowerKw);

    // Getter / Setter for "batteryLevel"
    default Integer getBatteryLevel(Staff staff) {
        if (staff.canViewVehicleInfo()) { return getBatteryLevel(); }
        else {throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);}}

    Integer getBatteryLevel();

    default void setBatteryLevel(Integer batteryLevel, Staff staff) {
        if (staff.canModifyVehicles()) { setBatteryLevel(batteryLevel);}
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    void setBatteryLevel(Integer batteryLevel);

    // Getter / Setter for "amountOfBatteries"
    default void setAmountOfBatteries(Integer amountOfBatteries, Staff staff) {
        if (staff.canModifyVehicles()) { setBatteryLevel(amountOfBatteries);}
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    void setAmountOfBatteries(Integer amountOfBatteries);
}
