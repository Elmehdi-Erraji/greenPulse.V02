package entities;

import entities.enums.TypeConsommation;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Alimentation extends CarbonRecord {
    private double foodConsumption;
    private String foodType;

    // Constructor
    public Alimentation(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double foodConsumption, String foodType) {
        super(startDate, endDate, amount, type, userId); // Call to the superclass constructor
        this.foodConsumption = foodConsumption;
        this.foodType = foodType;
        setUserId(userId); // Set userId using the setter from CarbonRecord
    }

    public Alimentation(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId) {
        super();
    }

    // Getters and setters
    public double getFoodConsumption() {
        return foodConsumption;
    }

    public void setFoodConsumption(double foodConsumption) {
        this.foodConsumption = foodConsumption;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    @Override
    public double calculateImpact() {
        // Example calculation (this should be replaced with actual logic)
        return foodConsumption * 0.20; // Assuming 0.20 is a conversion factor for impact
    }

    @Override
    public String toString() {
        return String.format("Alimentation [foodConsumption=%.2f, foodType=%s, %s]", foodConsumption, foodType, super.toString());
    }
}
