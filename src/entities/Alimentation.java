package entities;

import entities.enums.FoodType;
import entities.enums.TypeConsommation;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Alimentation extends CarbonRecord {
    private FoodType foodType;
    private double foodWeight;

    public Alimentation(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, FoodType foodType, double foodWeight) {
        super(startDate, endDate, amount, type, userId);
        this.foodType = foodType;
        this.foodWeight = foodWeight;
        this.impactValue = calculateImpact(); // Directly set impactValue
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
        this.impactValue = calculateImpact(); // Update impactValue directly
    }

    public double getFoodWeight() {
        return foodWeight;
    }

    public void setFoodWeight(double foodWeight) {
        this.foodWeight = foodWeight;
        this.impactValue = calculateImpact(); // Update impactValue directly
    }

    @Override
    public double calculateImpact() {
        return foodWeight * getImpactFactorByFoodType(foodType);
    }

    private double getImpactFactorByFoodType(FoodType type) {
        switch (type) {
            case MEAT:
                return 5.0;
            case VEGETABLES:
                return 0.5;
            default:
                return 1.0;
        }
    }

    @Override
    public String toString() {
        return String.format("Alimentation [foodType=%s, foodWeight=%.2f, impactValue=%.2f, %s]",
                foodType, foodWeight, impactValue, super.toString());
    }
}
