package entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class CarbonRecord {
    protected int id; // Add an id field
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected double amount;
    protected String type;
    protected int userId;  // Associate carbon record with a user

    public CarbonRecord(int id, LocalDate startDate, LocalDate endDate, double amount, String type, int userId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.type = type;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getAmount() {
        return BigDecimal.valueOf(amount);
    }

    public String getType() {
        return type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Abstract method to be implemented by subclasses
    public abstract double calculateImpact();

    @Override
    public String toString() {
        return "ID: " + id + ", Start Date: " + startDate + ", End Date: " + endDate + ", Amount: " + amount + " units, Type: " + type;
    }
}
