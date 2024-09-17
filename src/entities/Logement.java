package entities;

import entities.enums.EnergyType;
import entities.enums.TypeConsommation;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Logement extends CarbonRecord {
    private double energyConsumption;
    private EnergyType energyType;

    public Logement(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double energyConsumption, EnergyType energyType) {
        super(startDate, endDate, amount, type, userId);
        this.energyConsumption = energyConsumption;
        this.energyType = energyType;
        this.impactValue = calculateImpact();
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }


    public EnergyType getEnergyType() {
        return energyType;
    }


    @Override
    public double calculateImpact() {
        double factor = getImpactFactorByEnergyType(energyType);
        return energyConsumption * factor;
    }

    private double getImpactFactorByEnergyType(EnergyType type) {
        switch (type) {
            case ELECTRICITY:
                return 1.5;
            case GAS:
                return 2.0;
            default:
                return 1.0;
        }
    }

    @Override
    public String toString() {
        return String.format("Logement [energyConsumption=%.2f, energyType=%s, impactValue=%.2f, %s]",
                energyConsumption, energyType, impactValue, super.toString());
    }
}
