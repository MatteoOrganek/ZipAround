package uk.ac.roehampton.ziparound.vehicles.vehicletypes;

import uk.ac.roehampton.ziparound.vehicles.Electric;
import uk.ac.roehampton.ziparound.vehicles.Vehicle;

public class Scooter extends Vehicle implements Electric {

    public Scooter(Integer vehicleID, String name, String type, String numberPlate, Float totalMiles, Integer maxSpeed) {
        super(vehicleID, name, type, numberPlate, totalMiles, maxSpeed);
    }

    public double getRemainingRange() {
        return 0;
    }

    public int getBatteryLevel() {
        return 0;
    }

    public void setBatteryLevel() {

    }
}
