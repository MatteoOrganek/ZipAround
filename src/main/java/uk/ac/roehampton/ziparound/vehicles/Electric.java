package uk.ac.roehampton.ziparound.vehicles;

public interface Electric {
    Integer AmountBatteries = null;
    Integer maxPowerKw = null;
    Float batteryLevel = null;

    int getBatteryLevel();
    void setBatteryLevel();

    double getRemainingRange();
}
