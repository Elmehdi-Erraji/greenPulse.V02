package entities;

import enums.TypeConsommation;
import java.time.LocalDate;

public abstract class CarbonRecord {
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected double amount;
    protected TypeConsommation type;

    public CarbonRecord(LocalDate startDate, LocalDate endDate, double amount, TypeConsommation type) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.type = type;
    }


    // Abstract method to be implemented by subclasses
    public abstract void calculerImpact();

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getAmount() {
        return amount;
    }

    public TypeConsommation getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Start Date: " + startDate + ", End Date: " + endDate + ", Amount: " + amount + " units, Type: " + type;
    }
}
