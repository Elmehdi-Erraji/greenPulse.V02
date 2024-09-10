package services;

import entities.*;
import repository.CarbonRecordRepository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarbonRecordService {

    private CarbonRecordRepository carbonRecordRepository;

    public CarbonRecordService(CarbonRecordRepository carbonRecordRepository) {
        this.carbonRecordRepository = carbonRecordRepository;
    }

    // Add Logement Record
    public void addLogementRecord(Logement logement) throws SQLException {
        if (logement == null) {
            throw new IllegalArgumentException("Logement record cannot be null.");
        }

        validateLogement(logement);

        try {
            carbonRecordRepository.addLogementRecord(logement);
        } catch (SQLException e) {
            System.err.println("Error adding logement record: " + e.getMessage());
            throw e; // rethrow to handle it at a higher level if necessary
        }
    }

    // Add Transport Record
    public void addTransportRecord(Transport transport) throws SQLException {
        if (transport == null) {
            throw new IllegalArgumentException("Transport record cannot be null.");
        }

        validateTransport(transport);

        try {
            carbonRecordRepository.addTransportRecord(transport);
        } catch (SQLException e) {
            System.err.println("Error adding transport record: " + e.getMessage());
            throw e; // rethrow to handle it at a higher level if necessary
        }
    }

    // Add Alimentation Record
    public void addAlimentationRecord(Alimentation alimentation) throws SQLException {
        if (alimentation == null) {
            throw new IllegalArgumentException("Alimentation record cannot be null.");
        }

        validateAlimentation(alimentation);

        try {
            carbonRecordRepository.addAlimentationRecord(alimentation);
        } catch (SQLException e) {
            System.err.println("Error adding alimentation record: " + e.getMessage());
            throw e; // rethrow to handle it at a higher level if necessary
        }
    }

    // Delete a carbon record
    public void deleteCarbonRecord(int recordId) throws SQLException {
        if (recordId <= 0) {
            throw new IllegalArgumentException("Invalid record ID.");
        }

        try {
            carbonRecordRepository.deleteCarbonRecord(recordId);
        } catch (SQLException e) {
            System.err.println("Error deleting carbon record with ID " + recordId + ": " + e.getMessage());
            throw e;
        }
    }

    public ResultSet getAllRecordsByUserId(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        try {
            carbonRecordRepository.getAllRecordsByUserId(userId);
        } catch (SQLException e) {
            System.err.println("Error fetching records for user ID " + userId + ": " + e.getMessage());
            throw e; // rethrow to handle it at a higher level if necessary
        }
        return null;
    }

    // Validate Logement record
    private void validateLogement(Logement logement) {
        if (logement.getStartDate() == null || logement.getEndDate() == null) {
            throw new IllegalArgumentException("Logement dates cannot be null.");
        }
        if (logement.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Logement amount must be greater than zero.");
        }
        if (logement.getEnergyConsumption() <= 0) {
            throw new IllegalArgumentException("Logement energy consumption must be greater than zero.");
        }
    }

    // Validate Transport record
    private void validateTransport(Transport transport) {
        if (transport.getStartDate() == null || transport.getEndDate() == null) {
            throw new IllegalArgumentException("Transport dates cannot be null.");
        }
        if (transport.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transport amount must be greater than zero.");
        }
        if (transport.getDistance() <= 0) {
            throw new IllegalArgumentException("Transport distance must be greater than zero.");
        }
    }

    // Validate Alimentation record
    private void validateAlimentation(Alimentation alimentation) {
        if (alimentation.getStartDate() == null || alimentation.getEndDate() == null) {
            throw new IllegalArgumentException("Alimentation dates cannot be null.");
        }
        if (alimentation.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Alimentation amount must be greater than zero.");
        }
        if (alimentation.getFoodConsumption() <= 0) {
            throw new IllegalArgumentException("Alimentation food consumption must be greater than zero.");
        }
        if (alimentation.getFoodWeight() <= 0) {
            throw new IllegalArgumentException("Alimentation food weight must be greater than zero.");
        }
    }
}
