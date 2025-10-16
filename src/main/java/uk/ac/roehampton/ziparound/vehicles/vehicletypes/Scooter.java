package uk.ac.roehampton.ziparound.vehicles.vehicletypes;

import uk.ac.roehampton.ziparound.vehicles.Electric;
import uk.ac.roehampton.ziparound.vehicles.Vehicle;

public class Scooter extends Vehicle implements Electric {

    @Override
    public double getRemainingRange() {
        return 0;
    }

    @Override
    public int getBatteryLevel() {
        return 0;
    }

    @Override
    public void setBatteryLevel() {

    }
}
