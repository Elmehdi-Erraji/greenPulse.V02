package entities;

import java.time.LocalDate;

public class Logement extends CarbonRecord {
    private double energyConsumption;
    private String energyType;

    public Logement(LocalDate startDate, LocalDate endDate, double amount, double energyConsumption, String energyType, int userId) {
        super(startDate, endDate, amount, "LOGEMENT", userId);
        this.energyConsumption = energyConsumption;
        this.energyType = energyType;
    }

    // Getters and Setters
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
        // Implement impact calculation for Logement
        // Placeholder implementation; replace with actual logic
        return energyConsumption * 0.5; // Example: impact is energy consumption multiplied by a factor
    }

    @Override
    public String toString() {
        return super.toString() + ", Energy Consumption: " + energyConsumption + " kWh, Energy Type: " + energyType;
    }
}
