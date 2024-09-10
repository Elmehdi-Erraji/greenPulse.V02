package entities;

import entities.enums.TypeConsommation;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class CarbonRecord {
    protected int id;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected BigDecimal amount;
    protected TypeConsommation type;
    protected int userId;
    protected double impactValue;

    // Constructor with impactValue as double
    public CarbonRecord(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId, double impactValue) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.type = type;
        this.userId = userId;
        this.impactValue = impactValue;
    }

    // Constructor with default impactValue
    public CarbonRecord(LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId) {
        this(startDate, endDate, amount, type, userId, 0.0); // Default impactValue
    }

    // Default constructor
    public CarbonRecord() {}

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

    public double getImpactValue() {
        return impactValue;
    }

    public void setImpactValue(double impactValue) {
        this.impactValue = impactValue;
    }

    @Override
    public String toString() {
        return String.format("CarbonRecord [id=%d, startDate=%s, endDate=%s, amount=%s, type=%s, userId=%d, impactValue=%.2f]",
                id, startDate, endDate, amount, type, userId, impactValue);
    }

    public abstract double calculateImpact();


}
