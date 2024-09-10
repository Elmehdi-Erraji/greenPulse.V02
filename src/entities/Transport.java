package entities;

import entities.enums.TypeConsommation;
import entities.enums.VehicleType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Transport extends CarbonRecord {
    private double distance;
    private VehicleType vehicleType;

    // Constructor
    public Transport(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double distance, VehicleType vehicleType) {
        super(startDate, endDate, amount, type, userId);
        this.distance = distance;
        this.vehicleType = vehicleType; // Directly set as VehicleType
    }

    // Getters and setters
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public double calculateImpact() {
        return distance * 0.25; // Adjust this calculation as needed
    }

    @Override
    public String toString() {
        return String.format("Transport [distance=%.2f, vehicleType=%s, %s]", distance, vehicleType, super.toString());
    }
}
