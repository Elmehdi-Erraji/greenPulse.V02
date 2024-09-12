package services;

import entities.*;
import repository.CarbonRecordRepository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class CarbonRecordService {

    private CarbonRecordRepository carbonRecordRepository;

    public CarbonRecordService(CarbonRecordRepository carbonRecordRepository) {
        this.carbonRecordRepository = carbonRecordRepository;
    }

    public Optional<Boolean> addLogementRecord(Logement logement) {
        if (logement == null) {
            throw new IllegalArgumentException("Logement record cannot be null.");
        }

        validateLogement(logement);

        carbonRecordRepository.addLogementRecord(logement);
        return Optional.of(true); // Successfully added
    }

    public Optional<Boolean> addTransportRecord(Transport transport) {
        if (transport == null) {
            throw new IllegalArgumentException("Transport record cannot be null.");
        }

        validateTransport(transport);

        carbonRecordRepository.addTransportRecord(transport);
        return Optional.of(true); // Successfully added
    }

    public Optional<Boolean> addAlimentationRecord(Alimentation alimentation) {
        if (alimentation == null) {
            throw new IllegalArgumentException("Alimentation record cannot be null.");
        }

        validateAlimentation(alimentation);

        carbonRecordRepository.addAlimentationRecord(alimentation);
        return Optional.of(true); // Successfully added
    }

    public Optional<Boolean> deleteCarbonRecord(int recordId) {
        if (recordId <= 0) {
            throw new IllegalArgumentException("Invalid record ID.");
        }

        try {
            carbonRecordRepository.deleteCarbonRecord(recordId);
            return Optional.of(true); // Successfully deleted
        } catch (SQLException e) {
            System.err.println("Error deleting carbon record with ID " + recordId + ": " + e.getMessage());
            return Optional.empty(); // Indicating failure
        }
    }

    public Optional<List<Map<String, Object>>> getAllRecordsByUserId(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        List<Map<String, Object>> records = carbonRecordRepository.findAllByUserId(userId);
        return records.isEmpty() ? Optional.empty() : Optional.of(records);
    }

    private Optional<String> validateLogement(Logement logement) {
        if (logement.getStartDate() == null || logement.getEndDate() == null) {
            return Optional.of("Logement dates cannot be null.");
        }
        if (logement.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of("Logement amount must be greater than zero.");
        }
        if (logement.getEnergyConsumption() <= 0) {
            return Optional.of("Logement energy consumption must be greater than zero.");
        }
        return Optional.empty();
    }

    private Optional<String> validateTransport(Transport transport) {
        if (transport.getStartDate() == null || transport.getEndDate() == null) {
            return Optional.of("Transport dates cannot be null.");
        }
        if (transport.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of("Transport amount must be greater than zero.");
        }
        if (transport.getDistance() <= 0) {
            return Optional.of("Transport distance must be greater than zero.");
        }
        return Optional.empty();
    }

    private Optional<String> validateAlimentation(Alimentation alimentation) {
        if (alimentation.getStartDate() == null || alimentation.getEndDate() == null) {
            return Optional.of("Alimentation dates cannot be null.");
        }
        if (alimentation.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of("Alimentation amount must be greater than zero.");
        }
        if (alimentation.getFoodWeight() <= 0) {
            return Optional.of("Alimentation food weight must be greater than zero.");
        }
        return Optional.empty();
    }

}
