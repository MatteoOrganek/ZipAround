/**
 * Vehicle.java
 * Represents a general vehicle within the booking system.
 * Handles vehicle information and provides methods for management.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.vehicles;

import org.jetbrains.annotations.NotNull;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.staff.Staff;

/**
 * The Vehicle class stores and manages information about a vehicle.
 * It provides methods to set and retrieve details like ID, brand, and type.
 *
 * <p>This class is typically used by the booking and staff management system.</p>
 *
 * @see Staff
 * @see Booking
 * @see Electric
 */
public abstract class Vehicle {

    /** The unique identifier for this vehicle. */
    private Integer vehicleID;
    /** The brand of this vehicle. */
    private String brand;
    /** The type of vehicle. */
    private String type;
    /** This vehicle's number plate. */
    private String numberPlate;
    /** The total miles driven by this vehicle. */
    private Float totalMiles;
    /** The max speed for this vehicle in miles per hour. */
    private Integer maxSpeed;

    public Vehicle(Integer vehicleID,
                   String brand,
                   String type,
                   String numberPlate,
                   Float totalMiles,
                   Integer maxSpeed) {

        this.vehicleID = vehicleID;
        this.brand = brand;
        this.type = type;
        this.numberPlate = numberPlate;
        this.totalMiles = totalMiles;
        this.maxSpeed = maxSpeed;

    }

    // Getter / Setter for "vehicleID"
    public Integer getID(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return vehicleID; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setID(@NotNull Integer vehicleID, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.vehicleID = vehicleID; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "brand"
    public String getBrand(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return brand; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setBrand(@NotNull String name, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.brand = name; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "type"
    public String getType(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return type; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setType(@NotNull String type, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.type = type; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "numberPlate"
    public String getNumberPlate(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return numberPlate; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setNumberPlate(@NotNull String numberPlate, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.numberPlate = numberPlate; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "totalMiles"
    public Float getTotalMiles(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return totalMiles; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setTotalMiles(@NotNull Float totalMiles, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.totalMiles = totalMiles; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "maxSpeed"
    public Integer getMaxSpeed(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return maxSpeed; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setMaxSpeed(@NotNull Integer maxSpeed, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.maxSpeed = maxSpeed; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

}