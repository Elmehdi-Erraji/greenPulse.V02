package service;

import entities.CarbonRecord;
import repository.CarbonRecordRepository;

import java.sql.SQLException;
import java.util.List;

public class CarbonRecordService {
    private CarbonRecordRepository repository;

    public CarbonRecordService() throws SQLException {
        this.repository = new CarbonRecordRepository();
    }

    public void createCarbonRecord(CarbonRecord record) throws SQLException {
        repository.createCarbonRecord(record);
    }

    public CarbonRecord getCarbonRecordById(int id) throws SQLException {
        return repository.getCarbonRecordById(id);
    }

    public List<CarbonRecord> getAllCarbonRecords() throws SQLException {
        return repository.getAllCarbonRecords();
    }

    public void updateCarbonRecord(CarbonRecord record) throws SQLException {
        repository.updateCarbonRecord(record);
    }

    public void deleteCarbonRecord(int id) throws SQLException {
        repository.deleteCarbonRecord(id);
    }
}
