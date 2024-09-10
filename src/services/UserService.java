package services;

import entities.CarbonRecord;
import entities.User;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UserService {
    private UserRepository userRepository;
    private Connection connection;

    public UserService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        this.connection = connection;
        this.userRepository = new UserRepository(connection);
    }

    public void createUser(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        userRepository.createUser(user);
    }

    public User getUserById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.getUserById(id);
    }

    /*public List<User> getAllUsers() throws SQLException {
        return userRepository.getAllUsers();
    }*/

    public void updateUser(User user) throws SQLException {
        if (user == null || user.getId() <= 0) {
            throw new IllegalArgumentException("User or User ID cannot be null or invalid");
        }
        userRepository.updateUser(user);
    }

    public void deleteUser(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        userRepository.deleteUser(id);
    }

    public boolean isUserExist(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.isUserExist(userId);
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Set<User> getInactiveUsers(LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<Integer, User> userMap = userRepository.getAllUsersWithDetails();

        return userMap.values().stream()
                .filter(user -> user.getCarbonRecords().stream().noneMatch(record ->
                        !(record.getEndDate().isBefore(startDate) || record.getStartDate().isAfter(endDate))
                ))
                .collect(Collectors.toSet());
    }

    public double calculateAverageCarbonConsumption(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<User> users = userRepository.getAllUsersWithCarbonRecords(startDate, endDate);

        return users.stream()
                .mapToDouble(user -> {
                    // Fetch the total impact for each user from the list
                    double totalImpact = 0;
                    try {
                        totalImpact = getTotalImpactForUser(user.getId(), startDate, endDate);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return totalImpact;
                })
                .average()
                .orElse(0.0);
    }

    private double getTotalImpactForUser(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(cr.impact_value), 0) AS total_impact " +
                "FROM carbonrecords cr " +
                "WHERE cr.user_id = ? AND cr.start_date >= ? AND cr.end_date <= ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ((PreparedStatement) statement).setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(startDate));
            statement.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("total_impact");
                }
            }
        }
        return 0.0;
    }




    // Retrieves a map of user IDs to their total carbon impact
    private Map<Integer, Double> getUserImpactMap(LocalDate startDate, LocalDate endDate) {
        Map<Integer, Double> userImpactMap = new HashMap<>();

        try {
            List<User> users = userRepository.getAllUsers();

            for (User user : users) {
                double totalImpact = userRepository.getTotalImpactForUser(user.getId(), startDate, endDate);
                userImpactMap.put(user.getId(), totalImpact);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }

        return userImpactMap;
    }

    // Calculate average carbon consumption
    public double calculateAverageCarbonConsumption(LocalDate startDate, LocalDate endDate) {
        Map<Integer, Double> userImpactMap = getUserImpactMap(startDate, endDate);

        return userImpactMap.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    // Get users sorted by carbon consumption
    public List<User> getUsersSortedByCarbonConsumption(LocalDate startDate, LocalDate endDate) {
        Map<Integer, Double> userImpactMap = getUserImpactMap(startDate, endDate);

        return userImpactMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .map(entry -> new User(entry.getKey(), "", 0)) // Create a User object with id only
                .collect(Collectors.toList());
    }

    // Get users by carbon consumption threshold
    public List<User> getUsersByCarbonConsumptionThreshold(double threshold) {
        Map<Integer, Double> userImpactMap = getUserImpactMap(LocalDate.MIN, LocalDate.MAX);

        return userImpactMap.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .map(entry -> new User(entry.getKey(), "", 0)) // Create a User object with id only
                .collect(Collectors.toList());
    }



}
