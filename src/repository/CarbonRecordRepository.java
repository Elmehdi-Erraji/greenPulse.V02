package repository;

import entities.CarbonRecord;
import entities.Transport;
import entities.Logement;
import entities.Alimentation;
import entities.enums.TypeConsommation;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CarbonRecordRepository {

    private Connection connection;

    public CarbonRecordRepository(Connection connection) {
        this.connection = connection;
    }

    public void createCarbonRecord(CarbonRecord record) throws SQLException {
        String sql = "INSERT INTO carbon_records (start_date, end_date, amount, type, user_id, distance, vehicle_type, energy_consumption, energy_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(record.getStartDate()));
            statement.setDate(2, Date.valueOf(record.getEndDate()));
            statement.setBigDecimal(3, record.getAmount());
            statement.setString(4, record.getType().name());
            statement.setInt(5, record.getUserId());

            if (record instanceof Transport) {
                Transport transport = (Transport) record;
                statement.setDouble(6, transport.getDistance());
                statement.setString(7, transport.getVehicleType());
                statement.setNull(8, Types.NULL);
                statement.setNull(9, Types.NULL);
            } else if (record instanceof Logement) {
                Logement logement = (Logement) record;
                statement.setNull(6, Types.NULL);
                statement.setNull(7, Types.NULL);
                statement.setDouble(8, logement.getEnergyConsumption());
                statement.setString(9, logement.getEnergyType());
            } else if (record instanceof Alimentation) {
                Alimentation alimentation = (Alimentation) record;
                statement.setNull(6, Types.NULL);
                statement.setNull(7, Types.NULL);
                statement.setNull(8, Types.NULL);
                statement.setNull(9, Types.NULL);
            } else {
                throw new IllegalArgumentException("Unsupported CarbonRecord type");
            }

            statement.executeUpdate();
        }
    }

    public CarbonRecord getCarbonRecordById(int id) throws SQLException {
        String sql = "SELECT * FROM carbon_records WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                    LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    TypeConsommation type = TypeConsommation.valueOf(resultSet.getString("type"));
                    int userId = resultSet.getInt("user_id");

                    switch (type) {
                        case TRANSPORT:
                            return new Transport(
                                    startDate, endDate, amount, type, userId,
                                    resultSet.getDouble("distance"),
                                    resultSet.getString("vehicle_type")
                            );
                        case LOGEMENT:
                            return new Logement(
                                    startDate, endDate, amount, type, userId,
                                    resultSet.getDouble("energy_consumption"),
                                    resultSet.getString("energy_type")
                            );
                        case ALIMENTATION:
                            return new Alimentation(
                                    startDate, endDate, amount, type, userId,
                                    resultSet.getDouble("food_consumption"), // Example value for Alimentation
                                    resultSet.getString("food_type") // Example value for Alimentation
                            );
                        default:
                            throw new IllegalArgumentException("Unknown TypeConsommation value");
                    }
                }
            }
        }
        return null;
    }

    public List<CarbonRecord> getAllCarbonRecords() throws SQLException {
        List<CarbonRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM carbon_records";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                BigDecimal amount = resultSet.getBigDecimal("amount");
                TypeConsommation type = TypeConsommation.valueOf(resultSet.getString("type"));
                int userId = resultSet.getInt("user_id");

                CarbonRecord record = null;
                switch (type) {
                    case TRANSPORT:
                        record = new Transport(
                                startDate, endDate, amount, type, userId,
                                resultSet.getDouble("distance"),
                                resultSet.getString("vehicle_type")
                        );
                        break;
                    case LOGEMENT:
                        record = new Logement(
                                startDate, endDate, amount, type, userId,
                                resultSet.getDouble("energy_consumption"),
                                resultSet.getString("energy_type")
                        );
                        break;
                    case ALIMENTATION:
                        record = new Alimentation(
                                startDate, endDate, amount, type, userId,
                                resultSet.getDouble("food_consumption"),
                                resultSet.getString("food_type")
                        );
                        break;
                }
                if (record != null) {
                    records.add(record);
                }
            }
        }
        return records;
    }

    public void updateCarbonRecord(CarbonRecord record) throws SQLException {
        String sql = "UPDATE carbon_records SET start_date = ?, end_date = ?, amount = ?, type = ?, user_id = ?, distance = ?, vehicle_type = ?, energy_consumption = ?, energy_type = ?, food_consumption = ?, food_type = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(record.getStartDate()));
            statement.setDate(2, Date.valueOf(record.getEndDate()));
            statement.setBigDecimal(3, record.getAmount());
            statement.setString(4, record.getType().name());
            statement.setInt(5, record.getUserId());

            if (record instanceof Transport) {
                Transport transport = (Transport) record;
                statement.setDouble(6, transport.getDistance());
                statement.setString(7, transport.getVehicleType());
                statement.setNull(8, Types.NULL);
                statement.setNull(9, Types.NULL);
                statement.setNull(10, Types.NULL);
                statement.setNull(11, Types.NULL);
            } else if (record instanceof Logement) {
                Logement logement = (Logement) record;
                statement.setNull(6, Types.NULL);
                statement.setNull(7, Types.NULL);
                statement.setDouble(8, logement.getEnergyConsumption());
                statement.setString(9, logement.getEnergyType());
                statement.setNull(10, Types.NULL);
                statement.setNull(11, Types.NULL);
            } else if (record instanceof Alimentation) {
                Alimentation alimentation = (Alimentation) record;
                statement.setNull(6, Types.NULL);
                statement.setNull(7, Types.NULL);
                statement.setNull(8, Types.NULL);
                statement.setNull(9, Types.NULL);
                statement.setDouble(10, alimentation.getFoodConsumption());
                statement.setString(11, alimentation.getFoodType());
            } else {
                throw new IllegalArgumentException("Unsupported CarbonRecord type");
            }

            statement.setInt(12, record.getId());
            statement.executeUpdate();
        }
    }

    public void deleteCarbonRecord(int id) throws SQLException {
        String sql = "DELETE FROM carbon_records WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
