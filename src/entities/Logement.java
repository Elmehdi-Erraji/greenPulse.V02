package entities;

import java.time.LocalDate;

public class Logement extends CarbonRecord {

    public double consommationEnergie;
    public String typeEnergie;

    public Logement(LocalDate startDate, LocalDate endDate, double amount) {
        super(startDate, endDate, amount, "Logement");
    }

    @Override
    public double calculateImpact() {
        // Example: assume 0.233 kg CO2 per unit of energy usage for housing
        return amount * 0.233;
    }
}
