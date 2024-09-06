package entities;

import java.time.LocalDate;

public abstract class CarbonRecord {
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected double amount;
    protected String type;  // Now we take the type as a String input

    public CarbonRecord(LocalDate startDate, LocalDate endDate, double amount, String type) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    // Abstract method to be implemented by subclasses
    public abstract double calculateImpact();

    @Override
    public String toString() {
        return "Start Date: " + startDate + ", End Date: " + endDate + ", Amount: " + amount + " units, Type: " + type;
    }
}
