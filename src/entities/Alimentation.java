package entities;

import java.time.LocalDate;

public class Alimentation extends CarbonRecord {
    private String foodType;
    private double weight;

    public Alimentation(LocalDate startDate, LocalDate endDate, double amount, String foodType, double weight, int userId) {
        super(startDate, endDate, amount, "ALIMENTATION", userId);
        this.foodType = foodType;
        this.weight = weight;
    }

    // Getters and Setters
    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public double calculateImpact() {
        // Implement impact calculation for Alimentation
        // Placeholder implementation; replace with actual logic
        return weight * 0.2; // Example: impact is weight multiplied by a factor
    }

    @Override
    public String toString() {
        return super.toString() + ", Food Type: " + foodType + ", Weight: " + weight + " kg";
    }
}
