package entities;

import java.time.LocalDate;

public class Logement extends CarbonRecord {
    private double consommationEnergie; // energy consumption
    private String typeEnergie; // energy type (electricity, gas)

    public Logement(LocalDate startDate, LocalDate endDate, double consommationEnergie, String typeEnergie) {
        super(startDate, endDate);
        this.consommationEnergie = consommationEnergie;
        this.typeEnergie = typeEnergie;
    }

    @Override
    public void calculerImpact() {
        if ("electricite".equalsIgnoreCase(typeEnergie)) {
            this.amount = consommationEnergie * 1.5;
        } else if ("gaz".equalsIgnoreCase(typeEnergie)) {
            this.amount = consommationEnergie * 2.0;
        }
    }

    @Override
    public String toString() {
        return "Logement (" + typeEnergie + ") - Consommation: " + consommationEnergie + ", Impact: " + amount + " units";
    }
}
