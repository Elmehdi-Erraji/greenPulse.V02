package entities;

import java.time.LocalDate;

public class Transport extends CarbonRecord {
    private String typeDeVehicule;
    private double distanceParcourue;

    public Transport(LocalDate startDate, LocalDate endDate, double distanceParcourue, String typeDeVehicule) {
        super(startDate, endDate, 0, enums.TypeConsommation.TRANSPORT); // Set type to TRANSPORT
        this.typeDeVehicule = typeDeVehicule;
        this.distanceParcourue = distanceParcourue;
        calculerImpact();  // Calculate impact during construction
    }

    @Override
    public void calculerImpact() {
        if ("voiture".equalsIgnoreCase(typeDeVehicule)) {
            this.amount = distanceParcourue * 0.5;
        } else if ("train".equalsIgnoreCase(typeDeVehicule)) {
            this.amount = distanceParcourue * 0.1;
        } else {
            this.amount = 0;
        }
    }
}
