/**
 * Equipment.java
 * Handles equipment information and provides methods for management.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.equipment;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.equipment.maintenance.Maintainable;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.time.Instant;

public class Equipment implements Bookable, Maintainable {

    private final Integer id;
    private String name;
    private String model;
    private String description;
    private Boolean available;
    private Integer amountOfBookings;

    /**
     * Constructor for Equipment.
     * @param id Identifier for this Equipment
     * @param name Name of the equipment
     * @param description Description Of the Equipment
     * @param available Whether the Equipment is available for any bookings to be used. (note that the vehicle can be in a booking but still be available)
     */
    public Equipment(Integer id, String name, String model, String description, Boolean available, Integer amountOfBookings) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.description = description;
        this.available = available;
        this.amountOfBookings = amountOfBookings;
    }

    // Getter for "equipmentID"
    public Integer getID(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return id; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    // Getter / Setter for "name"
    public String getName(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return name; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setName(String name, Staff staff) {
        if (staff.canModifyEquipment()) { this.name = name; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "model"
    public String getModel(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return model; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setModel(String model, Staff staff) {
        if (staff.canModifyEquipment()) { this.model = model; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "description"
    public String getDescription(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return description; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setDescription(String description, Staff staff) {
        if (staff.canModifyEquipment()) { this.description = description; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "available"
    @Override
    public Boolean isAvailable(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return available; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setAvailable(Boolean available, Staff staff) {
        if (staff.canModifyEquipment()) { this.available = available; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    public void printInfo(Staff staff) {
        if (!staff.canViewEquipmentInfo()) {
            throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);
        }

        System.out.println("----- Equipment Details --------------------------------------------");
        System.out.println("Equipment ID  : " + (id != null ? id : "N/A"));
        System.out.println("Name          : " + (name != null ? name : "N/A"));
        System.out.println("Description   : " + (description != null ? description : "N/A"));
        System.out.println("Available     : " + (available != null ? (available ? "Yes" : "No") : "N/A"));
        System.out.println("--------------------------------------------------------------------");
    }

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
    public void setAmountOfBookings(Integer amountOfBookings, Staff staff) {
        if (staff.canModifyMaintenance()) { this.amountOfBookings = amountOfBookings; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }

    }

    @Override
    public Integer getAmountOfBookings(Staff staff) {
        if (staff.canViewMaintenanceInfo()) { return amountOfBookings; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

}
