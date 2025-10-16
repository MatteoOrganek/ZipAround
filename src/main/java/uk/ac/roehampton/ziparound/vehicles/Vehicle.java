package uk.ac.roehampton.ziparound.vehicles;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public abstract class Vehicle {

    // Vehicle description
    private String name;
    private String type;
    private String numberPlate;
    private String registrationNumber;
    private Integer totalMiles;
    private Integer maxSpeed;


    public Vehicle() {}

    public String getName(Staff s) {
        if (s.getAccessLevel() > 4) {
            return name;
        }
        else
        {
            System.out.println("Not Authorised");
            return null;
        }
    }

    public int setName(String name, Staff s) {
        if (s.getAccessLevel() > 4) {
            this.name = name;
            return 1;
        }
        else
        {
            System.out.println("Not Authorised");
            return 0;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Integer getTotalMiles() {
        return totalMiles;
    }

    public void setTotalMiles(Integer totalMiles) {
        this.totalMiles = totalMiles;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}