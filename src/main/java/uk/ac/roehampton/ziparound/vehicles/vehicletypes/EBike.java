/**
 * Ebike.java
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 *
 * @see uk.ac.roehampton.ziparound.vehicles.Vehicle
 */

package uk.ac.roehampton.ziparound.vehicles.vehicletypes;

import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.vehicles.Electric;
import uk.ac.roehampton.ziparound.vehicles.Vehicle;

/**
 * The Ebike class combines the Vehicle class and Electric Interface to
 * create a specific type of vehicle.
 *
 * <p>This class is typically used by the booking and staff management system.</p>
 *
 * @see Booking
 * @see Vehicle
 * @see Electric
 */
public class EBike extends Vehicle implements Electric {

    private Integer maxPowerKw;
    private Integer amountOfBatteries;
    private Integer batteryLevel = 100;

    public EBike(Integer vehicleID,
                 String brand,
                 String numberPlate,
                 Float totalMiles,
                 Integer maxPowerKw,
                 Integer amountOfBatteries) {
        this.vehicleID = vehicleID;
        this.brand = brand;
        this.type = "EBike";
        this.numberPlate = numberPlate;
        this.totalMiles = totalMiles;
        this.maxSpeed = 15;
        this.maxPowerKw = maxPowerKw;
        this.amountOfBatteries = amountOfBatteries;
    }


    // Getter / Setter for "maxPowerKw"
    @Override public Integer getMaxPowerKw() { return maxPowerKw; }

    @Override public void setMaxPowerKw(Integer maxPowerKw) { this.maxPowerKw = maxPowerKw; }

    // Getter / Setter for "batteryLevel"
    @Override public Integer getBatteryLevel() { return batteryLevel; }

    @Override public void setBatteryLevel(Integer batteryLevel) { this.batteryLevel = batteryLevel; }

    // Getter / Setter for "amountOfBatteries"
    @Override public Integer getAmountOfBatteries() { return amountOfBatteries; }

    @Override public void setAmountOfBatteries(Integer amountOfBatteries) { this.amountOfBatteries = amountOfBatteries; }


}
