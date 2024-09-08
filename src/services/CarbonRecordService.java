package services;

import entities.CarbonRecord;
import repository.CarbonRecordRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CarbonRecordService {
    private CarbonRecordRepository carbonRecordRepository;

    public CarbonRecordService(Connection connection) {
        this.carbonRecordRepository = new CarbonRecordRepository(connection);
    }

    public void createCarbonRecord(CarbonRecord record) throws SQLException {
        carbonRecordRepository.createCarbonRecord(record);
    }

    public CarbonRecord getCarbonRecordById(int id) throws SQLException {
        return carbonRecordRepository.getCarbonRecordById(id);
    }

    public List<CarbonRecord> getAllCarbonRecords() throws SQLException {
        return carbonRecordRepository.getAllCarbonRecords();
    }

    public void deleteCarbonRecord(int id) throws SQLException {
        carbonRecordRepository.deleteCarbonRecord(id);
    }
}
