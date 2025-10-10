package uk.ac.roehampton.ziparound;

public class Vehicle implements Electric{

    // Vehicle description
    String name;
    String type;
    String numberPlate;
    String registrationNumber;
    Integer totalMiles;
    Integer maxSpeed;

    public Vehicle() {}

    public Vehicle
            (
            String name,
            String type,
            String numberPlate,
            String registrationNumber,
            Integer totalMiles,
            Integer maxSpeed
            )
        {
            this.name = name;
            this.type = type;
            this.numberPlate = numberPlate;
            this.registrationNumber = registrationNumber;
            this.totalMiles = totalMiles;
            this.maxSpeed = maxSpeed;
        }

}