package entities;

import java.time.LocalDate;
import enums.TypeConsommation;

public class Alimentation extends CarbonRecord {
    private double poids; // weight of food consumed
    private String typeAliment; // type of food (meat, vegetable)

    public Alimentation(LocalDate startDate, LocalDate endDate, double poids, String typeAliment) {
        super(startDate, endDate, 0, TypeConsommation.ALIMENTATION); // Set type to ALIMENTATION and amount to 0 initially
        this.poids = poids;
        this.typeAliment = typeAliment;
        calculerImpact();  // Calculate impact during construction
    }

    @Override
    public void calculerImpact() {
        if ("viande".equalsIgnoreCase(typeAliment)) {
            this.amount = poids * 5.0;
        } else if ("legume".equalsIgnoreCase(typeAliment)) {
            this.amount = poids * 0.5;
        }
    }

    @Override
    public String toString() {
        return "Alimentation (" + typeAliment + ") - Poids: " + poids + ", Impact: " + amount + " units";
    }
}
