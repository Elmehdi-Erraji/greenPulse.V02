package entities;
import java.time.LocalDate;
public class Transport extends CarbonRecord{

    private double distanceParcourue;
    private String typeDeVehicule;

    public Transport(LocalDate startDate, LocalDate endDate, double distanceParcourue, String typeDeVehicule) {
        super(startDate, endDate);
        this.distanceParcourue = distanceParcourue;
        this.typeDeVehicule = typeDeVehicule;
    }

    @Override
    public void calculerImpact() {
        if("voiture".equalsIgnoreCase(typeDeVehicule)){//ignoring case sensitivity. This means that "voiture", "VOITURE", and "Voiture" would all be treated as equal.
            this.amount = distanceParcourue * 0.5;
        }else if ("train".equalsIgnoreCase(typeDeVehicule)) {
            this.amount = distanceParcourue * 0.1;
        }
    }
    @Override
    public String toString(){
        return "Transport (" + typeDeVehicule + ") - Distance: " + distanceParcourue + ", Impact: " + amount + " units";
    }
}
