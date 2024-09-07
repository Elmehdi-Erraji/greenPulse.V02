package repository;

import config.Database;
import entities.CarbonRecord;
import entities.Transport; // Example subclass
import entities.Logement; // Example subclass
import entities.Alimentation; // Example subclass

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarbonRecordRepository {

    private Connection connection;

    public CarbonRecordRepository() throws SQLException {
        this.connection = Database.getInstance().getConnection();
    }

    public void createCarbonRecord(CarbonRecord record) throws SQLException {
        String sql = "INSERT INTO carbonRecords (start_date, end_date, amount, type, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(record.getStartDate()));
            statement.setDate(2, Date.valueOf(record.getEndDate()));
            statement.setBigDecimal(3, record.getAmount());
            statement.setString(4, record.getType());
            statement.setInt(5, record.getUserId());
            statement.executeUpdate();
        }
    }

    public CarbonRecord getCarbonRecordById(int id) throws SQLException {
        String sql = "SELECT * FROM carbonRecords WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Example: instantiate based on type
                String type = resultSet.getString("type");
                switch (type) {
                    case "TRANSPORT":
                        return new Transport(
                                resultSet.getDate("start_date").toLocalDate(),
                                resultSet.getDate("end_date").toLocalDate(),
                                resultSet.getDouble("amount"),
                                resultSet.getDouble("distance"),
                                resultSet.getString("vehicle_type"),
                                resultSet.getInt("user_id")
                        );
                    case "LOGEMENT":
                        return new Logement(
                                resultSet.getDate("start_date").toLocalDate(),
                                resultSet.getDate("end_date").toLocalDate(),
                                resultSet.getDouble("amount"),
                                resultSet.getDouble("energy_consumption"),
                                resultSet.getString("energy_type"),
                                resultSet.getInt("user_id")
                        );
                    case "ALIMENTATION":
                        return new Alimentation(
                                resultSet.getDate("start_date").toLocalDate(),
                                resultSet.getDate("end_date").toLocalDate(),
                                resultSet.getDouble("amount"),
                                resultSet.getString("food_type"),
                                resultSet.getDouble("weight"),
                                resultSet.getInt("user_id")
                        );
                    default:
                        throw new SQLException("Unknown type: " + type);
                }
            }
        }
        return null;
    }

    public List<CarbonRecord> getAllCarbonRecords() throws SQLException {
        List<CarbonRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM carbonRecords";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                // Example: instantiate based on type
                String type = resultSet.getString("type");
                switch (type) {
                    case "TRANSPORT":
                        records.add(new Transport(
                                resultSet.getDate("start_date").toLocalDate(),
                                resultSet.getDate("end_date").toLocalDate(),
                                resultSet.getDouble("amount"),
                                resultSet.getDouble("distance"),
                                resultSet.getString("vehicle_type"),
                                resultSet.getInt("user_id")
                        ));
                        break;
                    case "LOGEMENT":
                        records.add(new Logement(
                                resultSet.getDate("start_date").toLocalDate(),
                                resultSet.getDate("end_date").toLocalDate(),
                                resultSet.getDouble("amount"),
                                resultSet.getDouble("energy_consumption"),
                                resultSet.getString("energy_type"),
                                resultSet.getInt("user_id")
                        ));
                        break;
                    case "ALIMENTATION":
                        records.add(new Alimentation(
                                resultSet.getDate("start_date").toLocalDate(),
                                resultSet.getDate("end_date").toLocalDate(),
                                resultSet.getDouble("amount"),
                                resultSet.getString("food_type"),
                                resultSet.getDouble("weight"),
                                resultSet.getInt("user_id")
                        ));
                        break;
                }
            }
        }
        return records;
    }

    public void updateCarbonRecord(CarbonRecord record) throws SQLException {
        String sql = "UPDATE carbonRecords SET start_date = ?, end_date = ?, amount = ?, type = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(record.getStartDate()));
            statement.setDate(2, Date.valueOf(record.getEndDate()));
            statement.setBigDecimal(3, record.getAmount());
            statement.setString(4, record.getType());
            statement.setInt(5, record.getUserId());
            statement.setInt(6, record.getId());
            statement.executeUpdate();
        }
    }

    public void deleteCarbonRecord(int id) throws SQLException {
        String sql = "DELETE FROM carbonRecords WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
