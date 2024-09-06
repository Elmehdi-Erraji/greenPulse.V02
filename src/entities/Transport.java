package entities;

import java.time.LocalDate;

public class Transport extends CarbonRecord {

    public double distanceParcourue ;
    public String typeDeVehicule;

    public Transport(LocalDate startDate, LocalDate endDate, double amount) {
        super(startDate, endDate, amount, "Transport");
    }

    @Override
    public double calculateImpact() {
        // Example: assume 2.31 kg CO2 per unit of transport usage
        return amount * 2.31;
    }
}
