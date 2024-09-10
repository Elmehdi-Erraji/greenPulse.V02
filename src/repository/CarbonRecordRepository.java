package repository;

import entities.*;
import entities.enums.TypeConsommation;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarbonRecordRepository {

    private Connection connection;

    public CarbonRecordRepository(Connection connection) {
        this.connection = connection;
    }

    public void addLogementRecord(Logement logement) throws SQLException {
        String insertCarbonRecordSql = "INSERT INTO carbonrecords (start_date, end_date, amount, type, user_id, impact_value) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        String insertLogementSql = "INSERT INTO logements (record_id, consommation_energie, type_energie) " +
                "VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            double impactValue = logement.getImpactValue();

            try (PreparedStatement insertCarbonRecordStmt = connection.prepareStatement(insertCarbonRecordSql)) {
                insertCarbonRecordStmt.setDate(1, Date.valueOf(logement.getStartDate()));
                insertCarbonRecordStmt.setDate(2, Date.valueOf(logement.getEndDate()));
                insertCarbonRecordStmt.setBigDecimal(3, logement.getAmount());
                insertCarbonRecordStmt.setString(4, TypeConsommation.LOGEMENT.name());
                insertCarbonRecordStmt.setInt(5, logement.getUserId());
                insertCarbonRecordStmt.setDouble(6, impactValue);

                ResultSet generatedKeys = insertCarbonRecordStmt.executeQuery();
                if (!generatedKeys.next()) {
                    throw new SQLException("Failed to insert carbon record.");
                }
                int recordId = generatedKeys.getInt(1);

                try (PreparedStatement insertLogementStmt = connection.prepareStatement(insertLogementSql)) {
                    insertLogementStmt.setInt(1, recordId);
                    insertLogementStmt.setDouble(2, logement.getEnergyConsumption());
                    insertLogementStmt.setString(3, logement.getEnergyType().name());

                    insertLogementStmt.executeUpdate();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Error adding logement record: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void addTransportRecord(Transport transport) throws SQLException {
        String insertCarbonRecordSql = "INSERT INTO carbonrecords (start_date, end_date, amount, type, user_id, impact_value) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        String insertTransportSql = "INSERT INTO transports (record_id, distance_parcourue, type_de_vehicule) " +
                "VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false); // Begin transaction

            // Calculate impact value using the method from the Transport subclass
            double impactValue = transport.getImpactValue();

            try (PreparedStatement insertCarbonRecordStmt = connection.prepareStatement(insertCarbonRecordSql)) {
                insertCarbonRecordStmt.setDate(1, Date.valueOf(transport.getStartDate()));
                insertCarbonRecordStmt.setDate(2, Date.valueOf(transport.getEndDate()));
                insertCarbonRecordStmt.setBigDecimal(3, transport.getAmount());
                insertCarbonRecordStmt.setString(4, TypeConsommation.TRANSPORT.name());
                insertCarbonRecordStmt.setInt(5, transport.getUserId());
                insertCarbonRecordStmt.setDouble(6, impactValue);

                ResultSet generatedKeys = insertCarbonRecordStmt.executeQuery();
                if (!generatedKeys.next()) {
                    throw new SQLException("Failed to insert carbon record.");
                }
                int recordId = generatedKeys.getInt(1);

                try (PreparedStatement insertTransportStmt = connection.prepareStatement(insertTransportSql)) {
                    insertTransportStmt.setInt(1, recordId);
                    insertTransportStmt.setDouble(2, transport.getDistance());
                    insertTransportStmt.setString(3, transport.getVehicleType().name());

                    insertTransportStmt.executeUpdate();
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction on error
            System.err.println("Error adding transport record: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true); // Reset auto-commit mode
        }
    }

    public void addAlimentationRecord(Alimentation alimentation) throws SQLException {
        String insertCarbonRecordSql = "INSERT INTO carbonrecords (start_date, end_date, amount, type, user_id, impact_value) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        String insertAlimentationSql = "INSERT INTO alimentations (record_id, type_aliment, poids) " +
                "VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            double impactValue = alimentation.getImpactValue();

            try (PreparedStatement insertCarbonRecordStmt = connection.prepareStatement(insertCarbonRecordSql)) {
                insertCarbonRecordStmt.setDate(1, Date.valueOf(alimentation.getStartDate()));
                insertCarbonRecordStmt.setDate(2, Date.valueOf(alimentation.getEndDate()));
                insertCarbonRecordStmt.setBigDecimal(3, alimentation.getAmount());
                insertCarbonRecordStmt.setString(4, TypeConsommation.ALIMENTATION.name());
                insertCarbonRecordStmt.setInt(5, alimentation.getUserId());
                insertCarbonRecordStmt.setDouble(6, impactValue);

                ResultSet generatedKeys = insertCarbonRecordStmt.executeQuery();
                if (!generatedKeys.next()) {
                    throw new SQLException("Failed to insert carbon record.");
                }
                int recordId = generatedKeys.getInt(1);

                try (PreparedStatement insertAlimentationStmt = connection.prepareStatement(insertAlimentationSql)) {
                    insertAlimentationStmt.setInt(1, recordId);
                    insertAlimentationStmt.setString(2, alimentation.getFoodType().name());
                    insertAlimentationStmt.setDouble(3, alimentation.getFoodWeight());

                    insertAlimentationStmt.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Error adding alimentation record: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true); // Reset auto-commit mode
        }
    }

    public void deleteCarbonRecord(int recordId) throws SQLException {
        String deleteCarbonRecordSql = "DELETE FROM carbonrecords WHERE id = ?";

        try (PreparedStatement deleteCarbonRecordStmt = connection.prepareStatement(deleteCarbonRecordSql)) {
            deleteCarbonRecordStmt.setInt(1, recordId);

            int rowsAffected = deleteCarbonRecordStmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No carbon record found with ID " + recordId);
            } else {
                System.out.println("Carbon record with ID " + recordId + " deleted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting carbon record: " + e.getMessage());
            throw e;
        }
    }

    public List<Map<String, Object>> findAllByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM carbonrecords WHERE user_id = ?";
        List<Map<String, Object>> recordsList = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> recordMap = new HashMap<>();
                    recordMap.put("id", rs.getInt("id"));
                    recordMap.put("start_date", rs.getDate("start_date").toLocalDate());
                    recordMap.put("end_date", rs.getDate("end_date").toLocalDate());
                    recordMap.put("amount", rs.getBigDecimal("amount"));
                    recordMap.put("type", rs.getString("type"));

                    recordsList.add(recordMap);
                }
            }
        }

        return recordsList;
    }}
