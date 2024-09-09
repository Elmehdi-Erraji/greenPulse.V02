package entities;

import entities.enums.TypeConsommation;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class CarbonRecord {
    private int id;  // Use long if id might exceed int
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amount;
    private TypeConsommation type;
    private int userId;

    // Constructor with parameters
    public CarbonRecord(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.type = type;
        this.userId = userId;
    }

    // Default constructor
    public CarbonRecord() {}

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TypeConsommation getType() {
        return type;
    }

    public void setType(TypeConsommation type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return String.format("CarbonRecord [id=%d, startDate=%s, endDate=%s, amount=%s, type=%s, userId=%d]",
                id, startDate, endDate, amount, type, userId);
    }

    // Abstract method for subclasses to implement
    public abstract double calculateImpact();
}
