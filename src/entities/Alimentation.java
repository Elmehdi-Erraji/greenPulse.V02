package entities;

import java.time.LocalDate;

public class Alimentation extends CarbonRecord {

    public String typeAliment;
    public double poids;

    public Alimentation(LocalDate startDate, LocalDate endDate, double amount) {
        super(startDate, endDate, amount, "Alimentation");
    }

    @Override
    public double calculateImpact() {
        // Example: assume 5 kg CO2 per unit of food consumption
        return amount * 5;
    }
}
