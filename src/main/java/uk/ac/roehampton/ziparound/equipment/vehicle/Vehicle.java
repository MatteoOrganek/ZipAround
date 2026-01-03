/**
 * Vehicle.java
 * Represents a general vehicle within the booking system.
 * Handles vehicle information and provides methods for management.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.equipment.vehicle;

import org.jetbrains.annotations.NotNull;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.staff.Staff;

/**
 * The Vehicle abstract class stores and manages information about a vehicle.
 * It provides methods to set and retrieve details like ID, brand, and type.
 *
 * <p>This class is typically used by the booking and staff management system.</p>
 *
 * @see Staff
 * @see Booking
 * @see Electric
 */
public abstract class Vehicle implements Bookable {

    /** The unique identifier for this vehicle. */
    protected Integer id;
    /** The brand of this vehicle. */
    protected String brand;
    /** The model of this vehicle. */
    protected String model;
    /** The type of vehicle. */
    protected String type;
    /** This vehicle's number plate. */
    protected String numberPlate;
    /** The total miles driven by this vehicle. */
    protected Float totalMiles;
    /** Whether the vehicle is available for any bookings to be used. (note that the vehicle can be in a booking but still be available) */
    protected Boolean available;

    // Getter / Setter for "id"
    public Integer getID(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return id; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    // Getter / Setter for "name"
    public String getName(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return brand; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
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

    // Getter / Setter for "model"
    public String  getModel(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return model; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setModel(@NotNull String model, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.model = model; }
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

    // Getter / Setter for "available"
    public Boolean isAvailable(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return available; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setAvailable(@NotNull Boolean available, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.available = available; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }


    public void printInfo(@NotNull Staff staff) {
        if (!staff.canViewVehicleInfo()) {
            throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);
        }

        System.out.println("----- Vehicle Details ----------------------------------------------");
        System.out.println("Vehicle ID     : " + (id != null ? id : "N/A"));
        System.out.println("Brand          : " + (brand != null ? brand : "N/A"));
        System.out.println("Model          : " + (model != null ? model : "N/A"));
        System.out.println("Type           : " + (type != null ? type : "N/A"));
        System.out.println("Number Plate   : " + (numberPlate != null ? numberPlate : "N/A"));
        System.out.println("Total Miles    : " + (totalMiles != null ? totalMiles + " mi" : "N/A"));
        System.out.println("Available      : " + (available != null ? (available ? "Yes" : "No") : "N/A"));
        System.out.println("--------------------------------------------------------------------");
    }

}