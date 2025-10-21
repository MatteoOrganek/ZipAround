package uk.ac.roehampton.ziparound.vehicles;

import org.jetbrains.annotations.NotNull;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.staff.Staff;

public abstract class Vehicle {

    // Vehicle description
    private Integer vehicleID;
    private String name;
    private String type;
    private String numberPlate;
    private Float totalMiles;
    private Integer maxSpeed;

    public Vehicle(Integer vehicleID, String name, String type, String numberPlate, Float totalMiles, Integer maxSpeed) {
        this.vehicleID = vehicleID;
        this.name = name;
        this.type = type;
        this.numberPlate = numberPlate;
        this.totalMiles = totalMiles;
        this.maxSpeed = maxSpeed;
    }

    // Getter / Setter for vehicleID
    public Integer getID(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return vehicleID; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setID(@NotNull Integer vehicleID, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.vehicleID = vehicleID; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for name
    public String getName(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return name; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setName(@NotNull String name, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.name = name; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for type
    public String getType(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return type; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setType(@NotNull String type, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.type = type; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for numberPlate
    public String getNumberPlate(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return numberPlate; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setNumberPlate(@NotNull String numberPlate, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.numberPlate = numberPlate; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for totalMiles
    public Float getTotalMiles(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return totalMiles; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setTotalMiles(@NotNull Float totalMiles, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.totalMiles = totalMiles; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for maxSpeed
    public Integer getMaxSpeed(@NotNull Staff staff) {
        if (staff.canViewVehicleInfo()) { return maxSpeed; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setMaxSpeed(@NotNull Integer maxSpeed, @NotNull Staff staff) {
        if (staff.canModifyVehicles()) { this.maxSpeed = maxSpeed; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

}