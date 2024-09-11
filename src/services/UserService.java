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
import java.time.temporal.WeekFields;
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

    public void generateConsumptionReport(int userId, int periodType, LocalDate startDate, LocalDate endDate) throws SQLException {

        User user = userRepository.getUserRecordsById(userId);

        if (user == null) {
            System.out.println("User with ID " + userId + " not found.");
            return;
        }

        List<CarbonRecord> records = user.getCarbonRecords();

        if (records == null || records.isEmpty()) {
            System.out.println("No carbon consumption records available for user " + user.getName());
            return;
        }

        // Filter records based on date range
        List<CarbonRecord> filteredRecords = records.stream()
                .filter(record -> {
                    LocalDate recordDate = record.getStartDate();
                    return !recordDate.isBefore(startDate) && !recordDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        if (filteredRecords.isEmpty()) {
            System.out.println("No carbon consumption records available in the specified date range.");
            return;
        }

        // Generate the report based on the period type
        switch (periodType) {
            case 1:  // Daily
                generateDailyReport(filteredRecords);
                break;
            case 2:  // Weekly
                generateWeeklyReport(filteredRecords);
                break;
            case 3:  // Monthly
                generateMonthlyReport(filteredRecords);
                break;
            default:
                System.out.println("Invalid period type.");
        }
    }

    private void generateDailyReport(List<CarbonRecord> records) {
        System.out.println("Daily Report for " + records.size() + " records:");
        // Group records by date and output daily consumption
        records.stream()
                .collect(Collectors.groupingBy(record -> record.getStartDate()))
                .forEach((date, dailyRecords) -> {
                    System.out.println("Date: " + date);
                    dailyRecords.forEach(record -> System.out.println(record));  // Customize this based on your CarbonRecord's toString() method
                });
    }

    private void generateWeeklyReport(List<CarbonRecord> records) {
        System.out.println("Weekly Report for " + records.size() + " records:");
        // Group records by week and output weekly consumption
        records.stream()
                .collect(Collectors.groupingBy(record -> getWeekOfYear(record.getStartDate())))
                .forEach((week, weeklyRecords) -> {
                    System.out.println("Week: " + week);
                    weeklyRecords.forEach(record -> System.out.println(record));  // Customize this based on your CarbonRecord's toString() method
                });
    }

    private void generateMonthlyReport(List<CarbonRecord> records) {
        System.out.println("Monthly Report for " + records.size() + " records:");
        // Group records by month and output monthly consumption
        records.stream()
                .collect(Collectors.groupingBy(record -> record.getStartDate().getMonth()))
                .forEach((month, monthlyRecords) -> {
                    System.out.println("Month: " + month);
                    monthlyRecords.forEach(record -> System.out.println(record));  // Customize this based on your CarbonRecord's toString() method
                });
    }

    private int getWeekOfYear(LocalDate date) {
        // This method returns the week number of the year for a given date
        return date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
    }

}
