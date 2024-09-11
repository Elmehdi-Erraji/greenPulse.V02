package repository;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import entities.*;
import entities.enums.EnergyType;
import entities.enums.FoodType;
import entities.enums.TypeConsommation;
import entities.enums.VehicleType;

public class UserRepository {

    private Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, age) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setInt(2, user.getAge());
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("age")
                    );
                }
            }
        }
        return null;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, age = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setInt(2, user.getAge());
            statement.setInt(3, user.getId());
            statement.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public boolean isUserExist(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String userSql = "SELECT * FROM users";
        String consumptionSql = "SELECT cr.id AS record_id, cr.start_date, cr.end_date, cr.amount, cr.type, " +
                "t.distance_parcourue, t.type_de_vehicule, " +
                "l.consommation_energie, l.type_energie, " +
                "a.type_aliment, a.poids " +
                "FROM carbonrecords cr " +
                "LEFT JOIN transports t ON cr.id = t.record_id " +
                "LEFT JOIN logements l ON cr.id = l.record_id " +
                "LEFT JOIN alimentations a ON cr.id = a.record_id " +
                "WHERE cr.user_id = ? ORDER BY cr.type";

        try (PreparedStatement userStatement = connection.prepareStatement(userSql);
             ResultSet userResultSet = userStatement.executeQuery()) {

            while (userResultSet.next()) {
                int userId = userResultSet.getInt("id");
                String name = userResultSet.getString("name");
                int age = userResultSet.getInt("age");

                User user = new User(userId, name, age);

                List<CarbonRecord> carbonRecords = new ArrayList<>();
                try (PreparedStatement consumptionStatement = connection.prepareStatement(consumptionSql)) {
                    consumptionStatement.setInt(1, userId);
                    try (ResultSet consumptionResultSet = consumptionStatement.executeQuery()) {
                        while (consumptionResultSet.next()) {
                            LocalDate startDate = consumptionResultSet.getDate("start_date") != null ?
                                    consumptionResultSet.getDate("start_date").toLocalDate() : null;
                            LocalDate endDate = consumptionResultSet.getDate("end_date") != null ?
                                    consumptionResultSet.getDate("end_date").toLocalDate() : null;
                            BigDecimal amount = consumptionResultSet.getBigDecimal("amount");
                            TypeConsommation type = TypeConsommation.valueOf(consumptionResultSet.getString("type"));

                            CarbonRecord record = null;
                            if (consumptionResultSet.getDouble("distance_parcourue") > 0) {
                                record = new Transport(
                                        startDate, endDate, amount, type, userId,
                                        consumptionResultSet.getDouble("distance_parcourue"),
                                        VehicleType.valueOf(consumptionResultSet.getString("type_de_vehicule"))
                                );
                            } else if (consumptionResultSet.getDouble("consommation_energie") > 0) {
                                record = new Logement(
                                        startDate, endDate, amount, type, userId,
                                        consumptionResultSet.getDouble("consommation_energie"),
                                        EnergyType.valueOf(consumptionResultSet.getString("type_energie"))
                                );
                            } else if (consumptionResultSet.getDouble("poids") > 0) {
                                record = new Alimentation(
                                        startDate, endDate, amount, type, userId,
                                        FoodType.valueOf(consumptionResultSet.getString("type_aliment")),
                                        consumptionResultSet.getDouble("poids")
                                );
                            }

                            if (record != null) {
                                carbonRecords.add(record);
                            }
                        }
                    }
                }

                user.setCarbonRecords(carbonRecords);

                users.add(user);
            }
        }

        return users;
    }

    public User getUserRecordsById(int userId) throws SQLException {
        String userSql = "SELECT * FROM users WHERE id = ?";
        String consumptionSql = "SELECT cr.id AS record_id, cr.start_date, cr.end_date, cr.amount, cr.type, " +
                "t.distance_parcourue, t.type_de_vehicule, " +
                "l.consommation_energie, l.type_energie, " +
                "a.type_aliment, a.poids " +
                "FROM carbonrecords cr " +
                "LEFT JOIN transports t ON cr.id = t.record_id " +
                "LEFT JOIN logements l ON cr.id = l.record_id " +
                "LEFT JOIN alimentations a ON cr.id = a.record_id " +
                "WHERE cr.user_id = ? ORDER BY cr.type";

        User user = null;

        try (PreparedStatement userStatement = connection.prepareStatement(userSql)) {
            userStatement.setInt(1, userId);
            try (ResultSet userResultSet = userStatement.executeQuery()) {
                if (userResultSet.next()) {
                    String name = userResultSet.getString("name");
                    int age = userResultSet.getInt("age");

                    user = new User(userId, name, age);

                    List<CarbonRecord> carbonRecords = new ArrayList<>();
                    try (PreparedStatement consumptionStatement = connection.prepareStatement(consumptionSql)) {
                        consumptionStatement.setInt(1, userId);
                        try (ResultSet consumptionResultSet = consumptionStatement.executeQuery()) {
                            while (consumptionResultSet.next()) {
                                LocalDate startDate = consumptionResultSet.getDate("start_date") != null ?
                                        consumptionResultSet.getDate("start_date").toLocalDate() : null;
                                LocalDate endDate = consumptionResultSet.getDate("end_date") != null ?
                                        consumptionResultSet.getDate("end_date").toLocalDate() : null;
                                BigDecimal amount = consumptionResultSet.getBigDecimal("amount");
                                TypeConsommation type = TypeConsommation.valueOf(consumptionResultSet.getString("type"));

                                CarbonRecord record = null;
                                if (consumptionResultSet.getDouble("distance_parcourue") > 0) {
                                    record = new Transport(
                                            startDate, endDate, amount, type, userId,
                                            consumptionResultSet.getDouble("distance_parcourue"),
                                            VehicleType.valueOf(consumptionResultSet.getString("type_de_vehicule"))
                                    );
                                } else if (consumptionResultSet.getDouble("consommation_energie") > 0) {
                                    record = new Logement(
                                            startDate, endDate, amount, type, userId,
                                            consumptionResultSet.getDouble("consommation_energie"),
                                            EnergyType.valueOf(consumptionResultSet.getString("type_energie"))
                                    );
                                } else if (consumptionResultSet.getDouble("poids") > 0) {
                                    record = new Alimentation(
                                            startDate, endDate, amount, type, userId,
                                            FoodType.valueOf(consumptionResultSet.getString("type_aliment")),
                                            consumptionResultSet.getDouble("poids")
                                    );
                                }

                                if (record != null) {
                                    carbonRecords.add(record);
                                }
                            }
                        }
                    }

                    user.setCarbonRecords(carbonRecords);
                }
            }
        }

        return user;
    }


}