package services;

import entities.CarbonRecord;
import entities.User;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public boolean createUser(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userRepository.createUser(user);
    }

    public boolean updateUser(User user) throws SQLException {
        if (user == null || user.getId() <= 0) {
            throw new IllegalArgumentException("User or User ID cannot be null or invalid");
        }
        return userRepository.updateUser(user);
    }

    public boolean deleteUser(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.deleteUser(id);
    }    public User getUserById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.getUserById(id);
    }

    public List<User> getAllUsers() throws SQLException {
        return userRepository.getAllUsers();
    }



    public Optional<Boolean> isUserExist(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return Optional.of(userRepository.isUserExist (userId)); // Assuming this returns a User or null
    }

    public Set<User> getInactiveUsers(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<User> users = userRepository.getAllUsers();

        return users.stream()
                .filter(user -> user.getCarbonRecords().stream().noneMatch(record ->
                        !(record.getEndDate().isBefore(startDate) || record.getStartDate().isAfter(endDate))
                ))
                .collect(Collectors.toSet());
    }

    public Map<User, Double> filterUsersByTotalConsumption(double threshold) throws SQLException {
        List<User> users = userRepository.getAllUsers(); // Get the list of users with their carbon records

        return users.stream()
                .map(user -> {
                    double totalConsumption = user.getCarbonRecords().stream()
                            .mapToDouble(CarbonRecord::getImpactValue)
                            .sum();
                    return new AbstractMap.SimpleEntry<>(user, totalConsumption);
                })
                .filter(entry -> entry.getValue() > threshold)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
        List<User> users = userRepository.getAllUsers();

        List<User> sortedUsers = users.stream()
                .map(user -> new User(user.getId(), user.getName(), user.getAge(), user.getCarbonRecords()) {
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


    public void generateConsumptionReport(int userId, int reportType, LocalDate startDate, LocalDate endDate) throws SQLException {

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

        switch (reportType) {
            case 1:
                generateDailyReport(filteredRecords, startDate, endDate);
                break;
            case 2:
                generateWeeklyReport(filteredRecords, startDate, endDate);
                break;
            case 3:
                generateMonthlyReport(filteredRecords, startDate, endDate);
                break;
            default:
                System.out.println("Invalid report type.");
        }
    }


    private void generateDailyReport(List<CarbonRecord> records, LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        double totalImpactValue = records.stream()
                .flatMap(record -> {
                    LocalDate recordStartDate = record.getStartDate();
                    LocalDate recordEndDate = record.getEndDate();

                    LocalDate effectiveStartDate = !recordStartDate.isBefore(startDate) ? recordStartDate : startDate;
                    LocalDate effectiveEndDate = !recordEndDate.isAfter(endDate) ? recordEndDate : endDate;

                    if (effectiveStartDate.isBefore(effectiveEndDate) || effectiveStartDate.isEqual(effectiveEndDate)) {
                        long daysInRecordPeriod = ChronoUnit.DAYS.between(effectiveStartDate, effectiveEndDate) + 1;
                        return Stream.of(totalDays > 0 ? (record.getImpactValue() * (double) daysInRecordPeriod / totalDays) : 0);
                    }
                    return Stream.empty();
                })
                .reduce(0.0, Double::sum);

        double averageImpactPerDay = totalImpactValue / totalDays;
        System.out.printf("Daily Report: Average Impact Value per Day: %.2f%n", averageImpactPerDay);
    }

    private void generateWeeklyReport(List<CarbonRecord> records, LocalDate startDate, LocalDate endDate) {
        List<CarbonRecord> filteredRecords = records.stream()
                .filter(record -> {
                    LocalDate recordStartDate = record.getStartDate();
                    LocalDate recordEndDate = record.getEndDate();
                    return !recordEndDate.isBefore(startDate) && !recordStartDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        Map<Integer, Double> weeklyImpact = filteredRecords.stream()
                .collect(Collectors.toMap(
                        record -> getWeekOfYear(record.getStartDate()),
                        record -> record.getStartDate().datesUntil(record.getEndDate().plusDays(1))
                                .filter(date -> !date.isBefore(startDate) && !date.isAfter(endDate))
                                .mapToDouble(date -> record.getImpactValue())
                                .sum(),
                        Double::sum
                ));

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalWeeks = totalDays / 7 + (totalDays % 7 == 0 ? 0 : 1);

        double totalImpactValue = weeklyImpact.values().stream().mapToDouble(v -> v).sum();
        double averageImpactValuePerWeek = totalImpactValue / totalWeeks;

        System.out.printf("Weekly Report:%nTotal Impact Value: %.2f%nAverage Impact Value per Week: %.2f%n", totalImpactValue, averageImpactValuePerWeek);

        System.out.println("Weekly Breakdown:");
        weeklyImpact.forEach((week, impact) ->
                System.out.printf("Week %d: Total Impact Value: %.2f%n", week, impact)
        );
    }

    private void generateMonthlyReport(List<CarbonRecord> records, LocalDate startDate, LocalDate endDate) {
        List<CarbonRecord> filteredRecords = records.stream()
                .filter(record -> {
                    LocalDate recordStartDate = record.getStartDate();
                    LocalDate recordEndDate = record.getEndDate();
                    return !recordEndDate.isBefore(startDate) && !recordStartDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        Map<String, Double> monthlyImpact = filteredRecords.stream()
                .collect(Collectors.toMap(
                        record -> getMonthYear(record.getStartDate()),
                        record -> record.getStartDate().datesUntil(record.getEndDate().plusDays(1))
                                .filter(date -> !date.isBefore(startDate) && !date.isAfter(endDate))
                                .mapToDouble(date -> record.getImpactValue())
                                .sum(),
                        Double::sum
                ));
        int totalMonths = (endDate.getYear() - startDate.getYear()) * 12 + endDate.getMonthValue() - startDate.getMonthValue() + 1;

        double totalImpactValue = monthlyImpact.values().stream().mapToDouble(v -> v).sum();
        double averageImpactValuePerMonth = totalImpactValue / totalMonths;

        System.out.printf("Monthly Report:%nTotal Impact Value: %.2f%nAverage Impact Value per Month: %.2f%n", totalImpactValue, averageImpactValuePerMonth);

        System.out.println("Monthly Breakdown:");
        monthlyImpact.forEach((monthYear, impact) ->
                System.out.printf("%s: Total Impact Value: %.2f%n", monthYear, impact)
        );
    }

    private int getWeekOfYear(LocalDate date) {
        return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    private String getMonthYear(LocalDate date) {
        return date.getMonth().name() + " " + date.getYear();
    }







}
