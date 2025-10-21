package uk.ac.roehampton.ziparound;

import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.vehicles.Vehicle;

import java.time.Instant;
import java.util.ArrayList;

public class Booking {

    // Booking info
    Instant bookedStartTime;
    Instant bookedEndTime;
    User user;

    ArrayList<Equipment> lEquipment;
    ArrayList<Vehicle> lVehicles;

    Boolean pendingApproval;
    Boolean approved;
    Staff staff;

    public Instant getBookedStartTime(Staff staff) {
        return bookedStartTime;
    }

    public void setBookedStartTime(Instant bookedStartTime, Staff staff) {
        this.bookedStartTime = bookedStartTime;
    }

    public Instant getBookedEndTime(Staff staff) {
        return bookedEndTime;
    }

    public void setBookedEndTime(Instant bookedEndTime, Staff staff) {
        this.bookedEndTime = bookedEndTime;
    }

    public User getUser(Staff staff) {
        return user;
    }

    public void setUser(User user, Staff staff) {
        this.user = user;
    }

    public ArrayList<Equipment> getlEquipment() {
        return lEquipment;
    }

    public void setlEquipment(ArrayList<Equipment> lEquipment) {
        this.lEquipment = lEquipment;
    }

    public ArrayList<Vehicle> getlVehicles() {
        return lVehicles;
    }

    public void setlVehicles(ArrayList<Vehicle> lVehicles) {
        this.lVehicles = lVehicles;
    }

    public Boolean getPendingApproval() {
        return pendingApproval;
    }

    public void setPendingApproval(Boolean pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
