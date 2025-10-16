package uk.ac.roehampton.ziparound;

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

}
