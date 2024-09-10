package services;

import entities.*;
import repository.CarbonRecordRepository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CarbonRecordService {

    private CarbonRecordRepository carbonRecordRepository;

    public CarbonRecordService(CarbonRecordRepository carbonRecordRepository) {
        this.carbonRecordRepository = carbonRecordRepository;
    }

    public void addLogementRecord(Logement logement) throws SQLException {
        if (logement == null) {
            throw new IllegalArgumentException("Logement record cannot be null.");
        }

        validateLogement(logement);

        try {
            carbonRecordRepository.addLogementRecord(logement);
        } catch (SQLException e) {
            System.err.println("Error adding logement record: " + e.getMessage());
            throw e;
        }
    }

    public void addTransportRecord(Transport transport) throws SQLException {
        if (transport == null) {
            throw new IllegalArgumentException("Transport record cannot be null.");
        }

        validateTransport(transport);

        try {
            carbonRecordRepository.addTransportRecord(transport);
        } catch (SQLException e) {
            System.err.println("Error adding transport record: " + e.getMessage());
            throw e;
        }
    }

    public void addAlimentationRecord(Alimentation alimentation) throws SQLException {
        if (alimentation == null) {
            throw new IllegalArgumentException("Alimentation record cannot be null.");
        }

        validateAlimentation(alimentation);

        try {
            carbonRecordRepository.addAlimentationRecord(alimentation);
        } catch (SQLException e) {
            System.err.println("Error adding alimentation record: " + e.getMessage());
            throw e;
        }
    }

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

    public List<Map<String, Object>> getAllRecordsByUserId(int userId) throws SQLException {
        return carbonRecordRepository.findAllByUserId(userId);
    }

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

    private void validateAlimentation(Alimentation alimentation) {
        if (alimentation.getStartDate() == null || alimentation.getEndDate() == null) {
            throw new IllegalArgumentException("Alimentation dates cannot be null.");
        }
        if (alimentation.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Alimentation amount must be greater than zero.");
        }
        if (alimentation.getFoodWeight() <= 0) {
            throw new IllegalArgumentException("Alimentation food weight must be greater than zero.");
        }
    }
}
