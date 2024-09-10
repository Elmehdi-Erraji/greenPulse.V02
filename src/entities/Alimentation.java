package entities;

import entities.enums.FoodType;
import entities.enums.TypeConsommation;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Alimentation extends CarbonRecord {
    private double foodConsumption;
    private FoodType foodType;
    private double foodWeight;


    public Alimentation(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double foodConsumption, FoodType foodType, double foodWeight) {
        super(startDate, endDate, amount, type, userId);
        this.foodType = foodType;
        this.foodWeight = foodWeight; // Set foodWeight properly
    }

    public double getFoodConsumption() {
        return foodConsumption;
    }

    public void setFoodConsumption(double foodConsumption) {
        this.foodConsumption = foodConsumption;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    public double getFoodWeight() {
        return foodWeight;
    }

    public void setFoodWeight(double foodWeight) {
        this.foodWeight = foodWeight;
    }

    @Override
    public double calculateImpact() {
        return foodConsumption * 0.20; // You can adjust this formula as per your requirement
    }

    @Override
    public String toString() {
        return String.format("Alimentation [foodConsumption=%.2f, foodType=%s, foodWeight=%.2f, %s]", foodConsumption, foodType, foodWeight, super.toString());
    }
}
