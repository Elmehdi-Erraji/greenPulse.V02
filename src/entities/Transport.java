package entities;

import entities.enums.TypeConsommation;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Transport extends CarbonRecord {
    private double distance;
    private String vehicleType;

    // Constructor
    public Transport(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double distance, String vehicleType) {
        super(startDate, endDate, amount, type, userId);
        this.distance = distance;
        this.vehicleType = vehicleType;
    }

    // Getters and setters
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public double calculateImpact() {
        // Example calculation (this should be replaced with actual logic)
        return distance * 0.25; // Assuming 0.25 is a conversion factor for impact
    }

    @Override
    public String toString() {
        return String.format("Transport [distance=%.2f, vehicleType=%s, %s]", distance, vehicleType, super.toString());
    }
}
