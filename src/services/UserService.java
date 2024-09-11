package services;

import entities.CarbonRecord;
import entities.User;
import repository.UserRepository;

import java.math.BigDecimal;
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

    public List<User> getAllUsers() throws SQLException {
        return userRepository.getAllUsers();
    }

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
        List<User> users = userRepository.getAllUsers(); // Get all users with their carbon records

        return users.stream()
                .filter(user -> user.getCarbonRecords().stream().noneMatch(record ->
                        !(record.getEndDate().isBefore(startDate) || record.getStartDate().isAfter(endDate))
                ))
                .collect(Collectors.toSet());
    }


    public List<User> filterUsersByTotalConsumption(double threshold) throws SQLException {
        List<User> users = userRepository.getAllUsers(); // Get the list of users with their carbon records

        return users.stream()
                .peek(user -> {
                    // Calculate the total carbon consumption for each user
                    double totalConsumption = user.getCarbonRecords().stream()
                            .mapToDouble(CarbonRecord::getImpactValue)
                            .sum();

                })
                .filter(user -> {
                    double totalConsumption = user.getCarbonRecords().stream()
                            .mapToDouble(CarbonRecord::getImpactValue)
                            .sum();

                    // Return true if the total consumption exceeds the threshold
                    return totalConsumption > threshold;
                })
                .collect(Collectors.toList());
    }

    public double calculateAverageCarbonConsumption(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<User> users = userRepository.getAllUsers(); // Retrieve all users with their carbon records

        double totalConsumption = users.stream()
                .flatMap(user -> user.getCarbonRecords().stream())
                .filter(record -> !record.getEndDate().isBefore(startDate) && !record.getStartDate().isAfter(endDate))
                .mapToDouble(CarbonRecord::getImpactValue)
                .sum();

        long totalRecords = users.stream()
                .flatMap(user -> user.getCarbonRecords().stream())
                .filter(record -> !record.getEndDate().isBefore(startDate) && !record.getStartDate().isAfter(endDate))
                .count();

        return totalRecords > 0 ? totalConsumption / totalRecords : 0;
    }

    public List<User> sortUsersByTotalCarbonConsumption() throws SQLException {
        List<User> users = userRepository.getAllUsers(); // Retrieve all users with their carbon records

        // Calculate total consumption for each user and sort in descending order
        List<User> sortedUsers = users.stream()
                .map(user -> new User(user.getId(), user.getName(), user.getAge(), user.getCarbonRecords()) {
                    // Use an anonymous subclass to calculate total consumption
                    @Override
                    public String toString() {
                        double totalConsumption = getCarbonRecords().stream()
                                .mapToDouble(CarbonRecord::getImpactValue)
                                .sum();
                        return String.format("User ID: %d, Name: %s, Total Consumption: %.2f KgCO2eq",
                                getId(), getName(), totalConsumption);
                    }
                })
                .sorted((u1, u2) -> {
                    double totalConsumption1 = u1.getCarbonRecords().stream()
                            .mapToDouble(CarbonRecord::getImpactValue)
                            .sum();
                    double totalConsumption2 = u2.getCarbonRecords().stream()
                            .mapToDouble(CarbonRecord::getImpactValue)
                            .sum();
                    return Double.compare(totalConsumption2, totalConsumption1); // Descending order
                })
                .collect(Collectors.toList());

        return sortedUsers;
    }


}
