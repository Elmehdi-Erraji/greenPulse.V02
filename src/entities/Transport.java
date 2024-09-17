package entities;

import entities.enums.TypeConsommation;
import entities.enums.VehicleType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Transport extends CarbonRecord {
    private double distance;
    private VehicleType vehicleType;

    public Transport(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double distance, VehicleType vehicleType) {
        super(startDate, endDate, amount, type, userId);
        this.distance = distance;
        this.vehicleType = vehicleType;
        this.impactValue = calculateImpact();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
        this.impactValue = calculateImpact();
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        this.impactValue = calculateImpact();
    }

    @Override
    public double calculateImpact() {
        return distance * getImpactFactorByVehicleType(vehicleType);
    }

    private double getImpactFactorByVehicleType(VehicleType vehicleType) {
        switch (vehicleType) {
            case CAR:
                return 0.5;
            case TRAIN:
                return 0.1;
            default:
                return 1.0;
        }
    }

    @Override
    public String toString() {
        return String.format("Transport [distance=%.2f, vehicleType=%s, impactValue=%.2f, %s]",
                distance, vehicleType, impactValue, super.toString());
    }
}
