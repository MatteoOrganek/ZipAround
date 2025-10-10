package uk.ac.roehampton.ziparound;

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
