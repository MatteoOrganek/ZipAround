/**
 * Ebike.java
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 *
 * @see uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle
 */

package uk.ac.roehampton.ziparound.equipment.vehicle.type;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.equipment.maintenance.Maintainable;
import uk.ac.roehampton.ziparound.equipment.vehicle.Electric;
import uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.time.Instant;

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
public class EBike extends Vehicle implements Electric, Maintainable {

    private Integer maxPowerKw;
    private Integer amountOfBatteries;
    private Integer batteryLevel = 100;
    private Booking lastInspection;

    public EBike(Integer id,
                 String brand,
                 String model,
                 String numberPlate,
                 Float totalMiles,
                 Boolean available,
                 Integer maxPowerKw,
                 Integer amountOfBatteries) {
        this.id = id;
        this.brand = brand;
        this.type = "EBike";
        this.model = model;
        this.numberPlate = numberPlate;
        this.totalMiles = totalMiles;
        this.available = available;
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


    @Override
    public void bookInspection(Instant startTime, Instant endTime) {
        BookingManager bookingManager = BookingManager.getInstance();
        try {
            bookingManager.addBooking(startTime, endTime, new SelfService(), this);
        } catch (BookingManager.BookingException e) {
            System.out.println("Cannot book at this time. Please retry.");
        }
    }

    @Override
    public void setLastInspection(Booking lastInspection, Staff staff) {
        if (staff.canModifyMaintenance()) { this.lastInspection = lastInspection; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    @Override
    public Booking getLastInspection(Staff staff) {
        if (staff.canViewMaintenanceInfo()) { return lastInspection; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }
}
