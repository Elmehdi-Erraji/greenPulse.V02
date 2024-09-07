package entities;

import java.time.LocalDate;

public class Transport extends CarbonRecord {
    private double distance;
    private String vehicleType;

    public Transport(LocalDate startDate, LocalDate endDate, double amount, double distance, String vehicleType, int userId) {
        super(startDate, endDate, amount, "TRANSPORT", userId);
        this.distance = distance;
        this.vehicleType = vehicleType;
    }

    // Getters and Setters
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
        // Implement impact calculation for Transport
        // Placeholder implementation; replace with actual logic
        return distance * 0.1; // Example: impact is distance multiplied by a factor
    }

    @Override
    public String toString() {
        return super.toString() + ", Distance: " + distance + " km, Vehicle Type: " + vehicleType;
    }
}
