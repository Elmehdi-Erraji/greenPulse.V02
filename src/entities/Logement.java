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
        // Example calculation (this should be replaced with actual logic)
        return energyConsumption * 0.15; // Assuming 0.15 is a conversion factor for impact
    }

    @Override
    public String toString() {
        return String.format("Logement [energyConsumption=%.2f, energyType=%s, %s]", energyConsumption, energyType, super.toString());
    }
}
