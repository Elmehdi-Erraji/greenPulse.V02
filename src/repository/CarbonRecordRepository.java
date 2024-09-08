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
        String sql = "INSERT INTO carbonRecords (start_date, end_date, amount, type, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(record.getStartDate()));
            statement.setDate(2, Date.valueOf(record.getEndDate()));
            statement.setBigDecimal(3, record.getAmount());
            statement.setString(4, record.getType().name());
            statement.setInt(5, record.getUserId());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    // Insert into related tables based on the type
                    insertIntoRelatedTable(record, generatedId);
                }
            }
        }
    }

    private void insertIntoRelatedTable(CarbonRecord record, int recordId) throws SQLException {
        String sql = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (record instanceof Transport) {
                Transport transport = (Transport) record;
                sql = "INSERT INTO transports (record_id, distance_parcourue, type_de_vehicule) VALUES (?, ?, ?)";
                statement.setInt(1, recordId);
                statement.setDouble(2, transport.getDistance());
                statement.setString(3, transport.getVehicleType());
            } else if (record instanceof Logement) {
                Logement logement = (Logement) record;
                sql = "INSERT INTO logements (record_id, consommation_energie, type_energie) VALUES (?, ?, ?)";
                statement.setInt(1, recordId);
                statement.setDouble(2, logement.getEnergyConsumption());
                statement.setString(3, logement.getEnergyType());
            } else if (record instanceof Alimentation) {
                Alimentation alimentation = (Alimentation) record;
                sql = "INSERT INTO alimentations (record_id, type_aliment, poids) VALUES (?, ?, ?)";
                statement.setInt(1, recordId);
                statement.setString(2, alimentation.getFoodType());
                statement.setDouble(3, alimentation.getFoodConsumption());
            } else {
                throw new IllegalArgumentException("Unsupported CarbonRecord type");
            }
            statement.executeUpdate();
        }
    }

    public CarbonRecord getCarbonRecordById(int id) throws SQLException {
        String sql = "SELECT * FROM carbonRecords WHERE id = ?";
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
                            return getTransportById(id, startDate, endDate, amount, type, userId);
                        case LOGEMENT:
                            return getLogementById(id, startDate, endDate, amount, type, userId);
                        case ALIMENTATION:
                            return getAlimentationById(id, startDate, endDate, amount, type, userId);
                        default:
                            throw new IllegalArgumentException("Unknown TypeConsommation value");
                    }
                }
            }
        }
        return null;
    }

    private Transport getTransportById(int id, LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId) throws SQLException {
        String sql = "SELECT * FROM transports WHERE record_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double distance = resultSet.getDouble("distance_parcourue");
                    String vehicleType = resultSet.getString("type_de_vehicule");
                    return new Transport(startDate, endDate, amount, type, userId, distance, vehicleType);
                }
            }
        }
        return null;
    }

    private Logement getLogementById(int id, LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId) throws SQLException {
        String sql = "SELECT * FROM logements WHERE record_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double energyConsumption = resultSet.getDouble("consommation_energie");
                    String energyType = resultSet.getString("type_energie");
                    return new Logement(startDate, endDate, amount, type, userId, energyConsumption, energyType);
                }
            }
        }
        return null;
    }

    private Alimentation getAlimentationById(int id, LocalDate startDate, LocalDate endDate, BigDecimal amount, TypeConsommation type, int userId) throws SQLException {
        String sql = "SELECT * FROM alimentations WHERE record_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String foodType = resultSet.getString("type_aliment");
                    double foodConsumption = resultSet.getDouble("poids");
                    return new Alimentation(startDate, endDate, amount, type, userId, foodConsumption, foodType);
                }
            }
        }
        return null;
    }

    public List<CarbonRecord> getAllCarbonRecords() throws SQLException {
        List<CarbonRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM carbonRecords";
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
                        record = getTransportById(resultSet.getInt("id"), startDate, endDate, amount, type, userId);
                        break;
                    case LOGEMENT:
                        record = getLogementById(resultSet.getInt("id"), startDate, endDate, amount, type, userId);
                        break;
                    case ALIMENTATION:
                        record = getAlimentationById(resultSet.getInt("id"), startDate, endDate, amount, type, userId);
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
        String sql = "UPDATE carbonRecords SET start_date = ?, end_date = ?, amount = ?, type = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(record.getStartDate()));
            statement.setDate(2, Date.valueOf(record.getEndDate()));
            statement.setBigDecimal(3, record.getAmount());
            statement.setString(4, record.getType().name());
            statement.setInt(5, record.getUserId());
            statement.setInt(6, record.getId());
            statement.executeUpdate();

            // Update related table
            updateRelatedTable(record);
        }
    }

    private void updateRelatedTable(CarbonRecord record) throws SQLException {
        String sql = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (record instanceof Transport) {
                Transport transport = (Transport) record;
                sql = "UPDATE transports SET distance_parcourue = ?, type_de_vehicule = ? WHERE record_id = ?";
                statement.setDouble(1, transport.getDistance());
                statement.setString(2, transport.getVehicleType());
            } else if (record instanceof Logement) {
                Logement logement = (Logement) record;
                sql = "UPDATE logements SET consommation_energie = ?, type_energie = ? WHERE record_id = ?";
                statement.setDouble(1, logement.getEnergyConsumption());
                statement.setString(2, logement.getEnergyType());
            } else if (record instanceof Alimentation) {
                Alimentation alimentation = (Alimentation) record;
                sql = "UPDATE alimentations SET type_aliment = ?, poids = ? WHERE record_id = ?";
                statement.setString(1, alimentation.getFoodType());
                statement.setDouble(2, alimentation.getFoodConsumption());
            } else {
                throw new IllegalArgumentException("Unsupported CarbonRecord type");
            }
            statement.setInt(3, record.getId());
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
