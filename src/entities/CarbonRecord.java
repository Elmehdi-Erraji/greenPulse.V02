package entities;

import java.time.LocalDate;

public abstract class CarbonRecord {
    private LocalDate startDate;
    private LocalDate endDate;
    protected double amount; // Accessible by subclasses

    public CarbonRecord(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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

    // Abstract method to be implemented by subclasses
    public abstract void calculerImpact();

    @Override
    public String toString() {
        return "Start Date: " + startDate + ", End Date: " + endDate + ", Amount: " + amount + " units";
    }
}
