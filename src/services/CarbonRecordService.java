package services;

import entities.enums.TypeConsommation;
import repository.CarbonRecordRepository;
import entities.CarbonRecord;
import entities.Transport;
import entities.Logement;
import entities.Alimentation;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CarbonRecordService {

    private CarbonRecordRepository repository;

    public CarbonRecordService(Connection connection) {
        this.repository = new CarbonRecordRepository(connection);
    }

    public void createCarbonRecord(CarbonRecord record) {
        try {
            repository.createCarbonRecord(record);
        } catch (SQLException e) {
            // Handle the exception appropriately (e.g., log it, rethrow it, etc.)
            e.printStackTrace();
        }
    }

    public CarbonRecord getCarbonRecordById(int id) {
        try {
            return repository.getCarbonRecordById(id);
        } catch (SQLException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }

    public List<CarbonRecord> getAllCarbonRecords() {
        try {
            return repository.getAllCarbonRecords();
        } catch (SQLException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }

    public void updateCarbonRecord(CarbonRecord record) {
        try {
            repository.updateCarbonRecord(record);
        } catch (SQLException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }
    }

    public void deleteCarbonRecord(int id) {
        try {
            repository.deleteCarbonRecord(id);
        } catch (SQLException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }
    }

    public List<CarbonRecord> getCarbonRecordsByType(TypeConsommation type) {
        try {
            return repository.getCarbonRecordsByType(type);
        } catch (SQLException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }

    // Additional methods to support business logic can be added here
}
