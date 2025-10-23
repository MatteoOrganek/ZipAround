/**
 * Booking.java
 * Booking skeleton.
 * Handles booking information and provides methods for management.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.booking;

import org.jetbrains.annotations.NotNull;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.users.Customer;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.vehicles.Vehicle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

public class Booking {

    // Booking info
    Instant bookedStartTime;
    Instant bookedEndTime;
    Instant createdOn;
    Customer customer;

    ArrayList<Equipment> lEquipment;
    ArrayList<Vehicle> lVehicles;

    Boolean approved;
    Staff staff;

    public Booking(Instant bookedStartTime,
                   Instant bookedEndTime,
                   Instant createdOn,
                   Customer customer,
                   ArrayList<Equipment> lEquipment,
                   ArrayList<Vehicle> lVehicles,
                   Boolean approved,
                   Staff staff) {
        this.bookedStartTime = bookedStartTime;
        this.bookedEndTime = bookedEndTime;
        this.createdOn = createdOn;
        this.customer = customer;
        this.lEquipment = lEquipment;
        this.lVehicles = lVehicles;
        this.approved = approved;
        this.staff = staff;
    }

    // Getter / Setter for "bookedStartTime" - Can be null
    public Instant getBookedStartTime(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return bookedStartTime; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setBookedStartTime(Instant bookedStartTime, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.bookedStartTime = bookedStartTime; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "bookedEndTime" - Can be null
    public Instant getBookedEndTime(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return bookedEndTime; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setBookedEndTime(Instant bookedEndTime, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.bookedEndTime = bookedEndTime; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "createdOn" - Can be null
    public Instant getCreatedOn(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return createdOn; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setCreatedOn(Instant createdOn, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.createdOn = createdOn; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "customer"
    public Customer getCustomer(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return customer; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setCustomer(@NotNull Customer customer, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.customer = customer; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "lEquipment"
    public ArrayList<Equipment> getLEquipment(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return lEquipment; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setLEquipment(@NotNull ArrayList<Equipment> lEquipment, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.lEquipment = lEquipment; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "lVehicles"
    public ArrayList<Vehicle> getLVehicles(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return lVehicles; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setLVehicles(@NotNull ArrayList<Vehicle> lVehicles, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.lVehicles = lVehicles; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "approved"
    public Boolean getApproved(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return approved; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setApproved(@NotNull Boolean approved, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.approved = approved; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "staff"
    public Staff getStaff(@NotNull Staff staff) {
        if (staff.canViewBookingInfo()) { return staff; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setStaff(Staff newStaff, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.staff = newStaff; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Adds an equipment to LEquipment
    public void addEquipment(Equipment equipment, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.lEquipment.add(equipment); }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Removes a specified equipment from the list of Equipments
    public void removeEquipment(Equipment equipment, @NotNull Staff staff) {
        if (staff.canModifyBookings()) {
            // for each equipment in lEquipment
            for (Equipment currentEquipment : lEquipment) {
                // Check if both ids are the same, in case the object was modified.
                if (Objects.equals(currentEquipment.getID(staff), equipment.getID(staff))) {
                    // Try to remove the element from the list
                    try { this.lEquipment.remove(currentEquipment); }
                    // Ignore any kind of exceptions
                    catch (Exception ignored) {}
                }
            }
        }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Adds a vehicle to lVehicles
    public void addVehicles(Vehicle vehicle, @NotNull Staff staff) {
        if (staff.canModifyBookings()) { this.lVehicles.add(vehicle); }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Removes a specified vehicle from the list of Vehicles
    public void removeVehicle(Vehicle vehicle, @NotNull Staff staff) {
        if (staff.canModifyBookings()) {
            // for each equipment in lVehicles
            for (Vehicle currentVehicle : lVehicles) {
                // Check if both ids are the same, in case the object was modified.
                if (Objects.equals(currentVehicle.getID(staff), vehicle.getID(staff))) {
                    // Try to remove the element from the list
                    try { this.lVehicles.remove(currentVehicle); }
                    // Ignore any kind of exceptions
                    catch (Exception ignored) {}
                }
            }
        }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Prints a comprehensive list of all the attributes in the current booking.
    public void printBookingInfo(@NotNull Staff staff) {
        if (!staff.canViewBookingInfo()) {
            throw new SecurityException(Utils.UNAUTHORIZED_ACCESS);
        }

        System.out.println("===== Booking Details =====");
        System.out.println("Booked Start Time : " + bookedStartTime);
        System.out.println("Booked End Time   : " + bookedEndTime);
        System.out.println("Created On        : " + createdOn);

        if (customer != null) {
            System.out.println("Customer Name     : " + customer.getFullName(staff));
            System.out.println("Customer ID       : " + customer.getID(staff));
        } else {
            System.out.println("Customer          : null");
        }

        System.out.println("Approved          : " + approved);
        System.out.println("Staff Responsible : " + (this.staff != null ? this.staff.getFullName(staff) : "null"));

        System.out.println("Equipment List:");
        // If lEquipment is not null and empty
        if (lEquipment != null && !lEquipment.isEmpty()) {
            for (Equipment eq : lEquipment) {
                System.out.println(" - ID: " + eq.getID(staff) + ", Name: " + eq.getName(staff)
                        + ", Description: " + eq.getDescription(staff)
                        + ", Available: " + eq.isAvailable(staff));
            }
        } else {
            System.out.println(" - None");
        }

        System.out.println("Vehicle List:");
        // If lVehicles is not null and empty
        if (lVehicles != null && !lVehicles.isEmpty()) {
            for (Vehicle v : lVehicles) {
                System.out.println(" - ID: " + v.getID(staff) + ", Type: " + v.getType(staff));
            }
        } else {
            System.out.println(" - None");
        }

        System.out.println("===========================");
    }

}
