package entities;

import entities.enums.EnergyType;
import entities.enums.TypeConsommation;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Logement extends CarbonRecord {
    private double energyConsumption;
    private EnergyType energyType;

    // Constructor
    public Logement(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double energyConsumption, EnergyType energyType) {
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

    public EnergyType getEnergyType() {
        return energyType;
    }

    public void setEnergyType(EnergyType energyType) {
        this.energyType = energyType;
    }

    // Override method to calculate carbon impact
    @Override
    public double calculateImpact() {
        double factor = getImpactFactorByEnergyType(energyType);
        return energyConsumption * factor; // Replace with actual formula
    }

    private double getImpactFactorByEnergyType(EnergyType type) {
        switch (type) {
            case ELECTRICITY:
                return 0.20;
            case GAS:
                return 0.25;
            case SOLAR:
                return 0.05;
            case WIND:
                return 0.01;
            default:
                return 0.15; // Default impact factor
        }
    }

    @Override
    public String toString() {
        return String.format("Logement [energyConsumption=%.2f, energyType=%s, %s]", energyConsumption, energyType, super.toString());
    }
}
