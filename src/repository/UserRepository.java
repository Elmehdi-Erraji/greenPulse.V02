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

    public Set<User> getInactiveUsers(LocalDate startDate, LocalDate endDate) throws SQLException {
        Set<User> inactiveUsers = new HashSet<>();
        String sql = "SELECT u.id, u.name " +
                "FROM users u " +
                "LEFT JOIN carbonrecords cr ON u.id = cr.user_id " +
                "GROUP BY u.id, u.name " +
                "HAVING SUM(" +
                "    CASE " +
                "        WHEN cr.start_date IS NULL OR cr.end_date IS NULL THEN 0 " +
                "        WHEN cr.start_date <= ? AND cr.end_date >= ? THEN 1 " +
                "        ELSE 0 " +
                "    END" +
                ") = 0";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(endDate));
            statement.setDate(2, java.sql.Date.valueOf(startDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    inactiveUsers.add(new User(id, name, 0)); // Assuming age is not needed
                }
            }
        }
        return inactiveUsers;
    }



    public List<User> getAllUsersWithDetails(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.age, cr.id AS record_id, cr.impact_value " +
                "FROM users u " +
                "LEFT JOIN carbonrecords cr ON u.id = cr.user_id " +
                "WHERE cr.start_date >= ? AND cr.end_date <= ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(startDate));
            statement.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int age = resultSet.getInt("age");
                    double impactValue = resultSet.getDouble("impact_value");

                    User user = users.stream()
                            .filter(u -> u.getId() == userId)
                            .findFirst()
                            .orElseGet(() -> {
                                User newUser = new User(userId, name, age);
                                users.add(newUser);
                                return newUser;
                            });

                    // Add the impact value to the user (assuming `User` class has a method to add impact records)
                    user.addImpactValue(impactValue); // Adjust this line according to your `User` class
                }
            }
        }

        return users;
    }
    // Gets the total carbon impact for a user
    public double getTotalImpactForUser(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        double totalImpact = 0.0;
        String sql = "SELECT COALESCE(SUM(cr.impact_value), 0) AS total_impact " +
                "FROM carbonrecords cr " +
                "WHERE cr.user_id = ? AND cr.start_date >= ? AND cr.end_date <= ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(startDate));
            statement.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalImpact = resultSet.getDouble("total_impact");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }

        return totalImpact;
    }

}