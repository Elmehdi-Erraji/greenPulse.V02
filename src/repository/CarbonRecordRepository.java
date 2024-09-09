package repository;

import entities.CarbonRecord;
import entities.Transport;
import entities.Logement;
import entities.Alimentation;
import entities.enums.TypeConsommation;

import java.sql.Connection;
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
        String sql = "INSERT INTO carbonrecords (start_date, end_date, amount, type, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(record.getStartDate()));
            statement.setDate(2, Date.valueOf(record.getEndDate()));
            statement.setBigDecimal(3, record.getAmount());
            statement.setString(4, record.getType().name());
            statement.setInt(5, record.getUserId());
            statement.executeUpdate();

            // Get the generated ID to insert into the related table
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int recordId = generatedKeys.getInt(1);
                    insertIntoRelatedTable(record, recordId);
                }
            }
        }
    }

    private void insertIntoRelatedTable(CarbonRecord record, int recordId) throws SQLException {
        String sql = getRelatedTableSql(record);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, recordId);

            if (record instanceof Transport) {
                Transport transport = (Transport) record;
                statement.setDouble(2, transport.getDistance());
                statement.setString(3, transport.getVehicleType());
            } else if (record instanceof Logement) {
                Logement logement = (Logement) record;
                statement.setDouble(2, logement.getEnergyConsumption());
                statement.setString(3, logement.getEnergyType());
            } else if (record instanceof Alimentation) {
                Alimentation alimentation = (Alimentation) record;
                statement.setString(2, alimentation.getFoodType());
                statement.setDouble(3, alimentation.getFoodConsumption());
            }
            statement.executeUpdate();
        }
    }

    private String getRelatedTableSql(CarbonRecord record) {
        if (record instanceof Transport) {
            return "INSERT INTO transports (record_id, distance_parcourue, type_de_vehicule) VALUES (?, ?, ?)";
        } else if (record instanceof Logement) {
            return "INSERT INTO logements (record_id, consommation_energie, type_energie) VALUES (?, ?, ?)";
        } else if (record instanceof Alimentation) {
            return "INSERT INTO alimentations (record_id, type_aliment, poids) VALUES (?, ?, ?)";
        } else {
            throw new IllegalArgumentException("Unsupported CarbonRecord type");
        }
    }

    public CarbonRecord getCarbonRecordById(int id) throws SQLException {
        String sql = "SELECT * FROM carbonrecords WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                    LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    TypeConsommation type = TypeConsommation.valueOf(resultSet.getString("type"));
                    int userId = resultSet.getInt("user_id");

                    return getCarbonRecordByType(type, id, startDate, endDate, amount, userId);
                }
            }
        }
        return null;
    }

    private CarbonRecord getCarbonRecordByType(TypeConsommation type, int id, LocalDate startDate, LocalDate endDate, BigDecimal amount, int userId) throws SQLException {
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
        String sql = "SELECT * FROM carbonrecords";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                BigDecimal amount = resultSet.getBigDecimal("amount");
                TypeConsommation type = TypeConsommation.valueOf(resultSet.getString("type"));
                int userId = resultSet.getInt("user_id");

                CarbonRecord record = getCarbonRecordByType(type, resultSet.getInt("id"), startDate, endDate, amount, userId);
                if (record != null) {
                    records.add(record);
                }
            }
        }
        return records;
    }

    public void updateCarbonRecord(CarbonRecord record) throws SQLException {
        String sql = "UPDATE carbonrecords SET start_date = ?, end_date = ?, amount = ?, type = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(record.getStartDate()));
            statement.setDate(2, Date.valueOf(record.getEndDate()));
            statement.setBigDecimal(3, record.getAmount());
            statement.setString(4, record.getType().name());
            statement.setInt(5, record.getUserId());
            statement.setInt(6, record.getId());
            statement.executeUpdate();
        }

        // Update related table
        insertIntoRelatedTable(record, record.getId());
    }

    public void deleteCarbonRecord(int id) throws SQLException {
        String sql = "DELETE FROM carbonrecords WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

        // Also delete from related table
        deleteFromRelatedTable(id);
    }

    private void deleteFromRelatedTable(int id) throws SQLException {
        // Delete from related table based on the record type
        String sql = "DELETE FROM transports WHERE record_id = ?;" +
                "DELETE FROM logements WHERE record_id = ?;" +
                "DELETE FROM alimentations WHERE record_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setInt(2, id);
            statement.setInt(3, id);
            statement.executeUpdate();
        }
    }

    public List<CarbonRecord> getCarbonRecordsByType(TypeConsommation type) throws SQLException {
        List<CarbonRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM carbonrecords WHERE type = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                    LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    int userId = resultSet.getInt("user_id");

                    CarbonRecord record = getCarbonRecordByType(type, resultSet.getInt("id"), startDate, endDate, amount, userId);
                    if (record != null) {
                        records.add(record);
                    }
                }
            }
        }
        return records;
    }
}
