package entities;

import entities.enums.TypeConsommation;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Logement extends CarbonRecord {
    private double energyConsumption;
    private String energyType;

    // Constructor
    public Logement(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double energyConsumption, String energyType) {
        super(startDate, endDate, amount, type, userId);
        this.energyConsumption = energyConsumption;
        this.energyType = energyType;
    }

    // Getters and setters
    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public String getEnergyType() {
        return energyType;
    }

    public void setEnergyType(String energyType) {
        this.energyType = energyType;
    }

    @Override
    public double calculateImpact() {
        return energyConsumption * 0.15; // Replace with the actual formula for impact calculation
    }

    @Override
    public String toString() {
        return String.format("Logement [energyConsumption=%.2f, energyType=%s, %s]", energyConsumption, energyType, super.toString());
    }
}
