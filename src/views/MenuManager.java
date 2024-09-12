package views;

import entities.*;
import entities.enums.EnergyType;
import entities.enums.FoodType;
import entities.enums.TypeConsommation;
import entities.enums.VehicleType;
import repository.CarbonRecordRepository;
import services.CarbonRecordService;
import services.UserService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static validation.InputValidator.*;

public class MenuManager {

    private static UserService userService;
    private static CarbonRecordService carbonRecordService;

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GreenPulse", "GreenPulse", "");
             Scanner scanner = new Scanner(System.in)) {

            userService = new UserService(connection);
            CarbonRecordRepository carbonRecordRepository = new CarbonRecordRepository(connection);
            carbonRecordService = new CarbonRecordService(carbonRecordRepository);

            while (true) {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1. User Management");
                System.out.println("2. Carbon Record Management");
                System.out.println("3. Exit");

                int choice = getValidIntegerInput(scanner, "Enter your choice: ");

                switch (choice) {
                    case 1:
                        userManagementMenu(scanner);
                        break;
                    case 2:
                        carbonRecordManagementMenu(scanner);
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void userManagementMenu(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n=== User Management Menu ===");
            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. View Inactive Users");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Calculate Average Carbon Consumption");
            System.out.println("7. Sort Users by Carbon Consumption");
            System.out.println("8. Filter Users by Carbon Consumption");
            System.out.println("9. Go Back");

            int choice = getValidIntegerInput(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    addUser(scanner);
                    break;
                case 2:

                    viewUsers();

                    break;
                case 3:
                    viewInactiveUsers(scanner);
                    break;
                case 4:
                    updateUser(scanner);
                    break;
                case 5:
                    deleteUser(scanner);
                    break;
                case 6:
                    calculateAverageCarbonConsumption(scanner);
                    break;
                case 7:
                    sortUsersByCarbonConsumption(scanner);
                    break;
                case 8:
                    filterUsersByCarbonConsumption(scanner);
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void carbonRecordManagementMenu(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n=== Carbon Record Management Menu ===");
            System.out.println("1. Add Carbon Record");
            System.out.println("2. View Carbon Records");
            System.out.println("3. Delete Carbon Record");
            System.out.println("4. Generate Consumption Report");  // New option for generating a report
            System.out.println("5. Go Back");

            int choice = getValidIntegerInput(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    addCarbonRecord(scanner);
                    break;
                case 2:
                    viewCarbonRecords(scanner);
                    break;
                case 3:
                    deleteCarbonRecord(scanner);
                    break;
                case 4:
                    generateCarbonConsumptionReport(scanner);  // New case for generating report
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }



    /*USER management starts here*/
    private static void addUser(Scanner scanner) throws SQLException {
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();
        int age = getValidIntegerInput(scanner, "Enter user age: ");

        User user = new User(0, name, age);
        userService.createUser(user);

        System.out.println("User added successfully!");
    }

    private static void viewUsers() throws SQLException {
        System.out.println("\n=== Users and their Carbon Records ===");

        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : users) {
                System.out.println("User ID: " + user.getId());
                System.out.println("Name: " + user.getName());
                System.out.println("Age: " + user.getAge());

                List<CarbonRecord> carbonRecords = user.getCarbonRecords();
                if (carbonRecords == null || carbonRecords.isEmpty()) {
                    System.out.println("No carbon records found for this user.");
                } else {
                    System.out.println("Carbon Records:");
                    for (CarbonRecord record : carbonRecords) {
                        if (record instanceof Alimentation) {
                            Alimentation alimentation = (Alimentation) record;
                            System.out.printf("Record ID: %d, Type: %s, Start Date: %s, End Date: %s, Amount: %.2f, Food Type: %s, Food Weight: %.2f, Impact Value: %.2f\n",
                                    record.getId(), record.getType(), record.getStartDate(), record.getEndDate(), record.getAmount(),
                                    alimentation.getFoodType(), alimentation.getFoodWeight(), alimentation.getImpactValue());
                        } else if (record instanceof Logement) {
                            Logement logement = (Logement) record;
                            System.out.printf("Record ID: %d, Type: %s, Start Date: %s, End Date: %s, Amount: %.2f, Energy Consumption: %.2f, Energy Type: %s, Impact Value: %.2f\n",
                                    record.getId(), record.getType(), record.getStartDate(), record.getEndDate(), record.getAmount(),
                                    logement.getEnergyConsumption(), logement.getEnergyType(), logement.getImpactValue());
                        } else if (record instanceof Transport) {
                            Transport transport = (Transport) record;
                            System.out.printf("Record ID: %d, Type: %s, Start Date: %s, End Date: %s, Amount: %.2f, Distance: %.2f, Vehicle Type: %s, Impact Value: %.2f\n",
                                    record.getId(), record.getType(), record.getStartDate(), record.getEndDate(), record.getAmount(),
                                    transport.getDistance(), transport.getVehicleType(), transport.getImpactValue());
                        }
                    }
                }

                System.out.println("\n-----------------------------\n");
            }
        }
    }

    private static void updateUser(Scanner scanner) throws SQLException {
        int id = getValidIntegerInput(scanner, "Enter user ID to update: ");

        System.out.print("Enter new user name: ");
        String name = scanner.nextLine();
        int age = getValidIntegerInput(scanner, "Enter new user age: ");

        User user = new User(id, name, age);
        userService.updateUser(user);

        System.out.println("User updated successfully!");
    }

    private static void deleteUser(Scanner scanner) throws SQLException {
        int id = getValidIntegerInput(scanner, "Enter user ID to delete: ");
        userService.deleteUser(id);
        System.out.println("User deleted successfully!");
    }

    private static void viewInactiveUsers(Scanner scanner) throws SQLException {
        System.out.println("\n=== View Inactive Users ===");
        LocalDate startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");
        LocalDate endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");

        Set<User> inactiveUsers = userService.getInactiveUsers(startDate, endDate);

        if (inactiveUsers.isEmpty()) {
            System.out.println("No inactive users found.");
        } else {
            for (User user : inactiveUsers) {
                System.out.printf("User [id=%d, name=%s]%n", user.getId(), user.getName());
            }
        }
    }

    private static void calculateAverageCarbonConsumption(Scanner scanner) throws SQLException {
        System.out.println("Enter start date (yyyy-mm-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.println("Enter end date (yyyy-mm-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        double averageConsumption = userService.calculateAverageCarbonConsumption(startDate,endDate);
        System.out.printf("Average Carbon Consumption from %s to %s is %.2f KgCO2eq\n", startDate, endDate, averageConsumption);
    }

    private static void sortUsersByCarbonConsumption(Scanner scanner) throws SQLException {
        List<User> sortedUsers = userService.sortUsersByTotalCarbonConsumption();

        System.out.println("\nUsers sorted by total carbon consumption:");
        for (User user : sortedUsers) {
            System.out.println(user.toString()); // Using the overridden toString() method to display user information
        }
    }

    private static void filterUsersByCarbonConsumption(Scanner scanner) throws SQLException {
        System.out.println("Enter the consumption threshold (KgCO2eq): ");
        double threshold = scanner.nextDouble();
        scanner.nextLine();

        // Get the filtered users and their total consumption
        Map<User, Double> filteredUsers = userService.filterUsersByTotalConsumption(threshold);

        System.out.println("\nUsers with consumption exceeding " + threshold + " KgCO2eq:");
        for (Map.Entry<User, Double> entry : filteredUsers.entrySet()) {
            User user = entry.getKey();
            double totalConsumption = entry.getValue();

            System.out.printf("User ID: %d, Name: %s, Total Consumption: %.2f KgCO2eq\n",
                    user.getId(), user.getName(), totalConsumption);
        }
    }
    /*USER management ends here*/

    /*CARBON management starts here*/
    private static void addCarbonRecord(Scanner scanner) throws SQLException {
        int userId = getValidIntegerInput(scanner, "Enter user ID: ");

        Optional<Boolean> userExistsOptional = userService.isUserExist(userId);

        if (userExistsOptional.isEmpty() || !userExistsOptional.get()) {
            System.out.println("User ID does not exist. Please enter a valid User ID.");
            return;
        }

        System.out.println("Select consumption type:");
        TypeConsommation type = getTypeConsommationFromInput(scanner);

        switch (type) {
            case LOGEMENT:
                addLogementRecord(scanner, userId);
                break;
            case TRANSPORT:
                addTransportRecord(scanner, userId);
                break;
            case ALIMENTATION:
                addAlimentationRecord(scanner, userId);
                break;
            default:
                System.out.println("Unknown consumption type.");
                break;
        }
    }

    private static void addLogementRecord(Scanner scanner, int userId) throws SQLException {
        LocalDate startDate;
        LocalDate endDate;

        // Loop to validate that the start date is before the end date
        while (true) {
            startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");
            endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");

            if (startDate.isBefore(endDate)) {
                break; // Valid dates, exit the loop
            } else {
                System.out.println("Error: Start date must be before the end date. Please try again.");
            }
        }

        BigDecimal amount = getValidBigDecimalInput(scanner, "Enter amount: ");
        double energyConsumption = getValidDoubleInput(scanner, "Enter energy consumption: ");
        EnergyType energyType = getEnergyTypeFromInput(scanner);

        Logement logement = new Logement(startDate, endDate, amount, TypeConsommation.LOGEMENT, userId, energyConsumption, energyType);
        carbonRecordService.addLogementRecord(logement);
        System.out.println("Logement carbon record added successfully!");
    }

    private static void addTransportRecord(Scanner scanner, int userId) throws SQLException {
        LocalDate startDate;
        LocalDate endDate;

        while (true) {
            startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");
            endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");

            if (startDate.isBefore(endDate)) {
                break;
            } else {
                System.out.println("Error: Start date must be before the end date. Please try again.");
            }
        }

        BigDecimal amount = getValidBigDecimalInput(scanner, "Enter amount: ");
        double distance = getValidDoubleInput(scanner, "Enter distance: ");
        VehicleType vehicleType = getVehicleTypeFromInput(scanner);

        Transport transport = new Transport(startDate, endDate, amount, TypeConsommation.TRANSPORT, userId, distance, vehicleType);
        carbonRecordService.addTransportRecord(transport);
        System.out.println("Transport carbon record added successfully!");
    }

    private static void addAlimentationRecord(Scanner scanner, int userId) throws SQLException {
        LocalDate startDate;
        LocalDate endDate;

        while (true) {
            startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");
            endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");

            if (startDate.isBefore(endDate)) {
                break;
            } else {
                System.out.println("Error: Start date must be before the end date. Please try again.");
            }
        }

        BigDecimal amount = getValidBigDecimalInput(scanner, "Enter amount: ");
        double foodWeight = getValidDoubleInput(scanner, "Enter food weight: ");
        FoodType foodType = getFoodTypeFromInput(scanner);

        Alimentation alimentation = new Alimentation(startDate, endDate, amount, TypeConsommation.ALIMENTATION, userId, foodType, foodWeight);
        carbonRecordService.addAlimentationRecord(alimentation);
        System.out.println("Alimentation carbon record added successfully!");
    }

    private static void deleteCarbonRecord(Scanner scanner) throws SQLException {
        System.out.println("\n=== Delete Carbon Record ===");

        int recordId = getValidIntegerInput(scanner, "Enter the carbon record ID to delete: ");

        System.out.println("Are you sure you want to delete record ID " + recordId + "? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            carbonRecordService.deleteCarbonRecord(recordId);
            System.out.println("Carbon record deleted successfully.");
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    private static void viewCarbonRecords(Scanner scanner) throws SQLException {
        System.out.println("\n=== View Carbon Records ===");

        int userId = getValidIntegerInput(scanner, "Enter user ID: ");

        try {
            Optional<List<Map<String, Object>>> optionalRecords = carbonRecordService.getAllRecordsByUserId(userId);

            // Handle the Optional<List<Map<String, Object>>> safely
            List<Map<String, Object>> records = optionalRecords.orElseThrow(() ->
                    new NoSuchElementException("No carbon records found for this user.")
            );

            // Sort and process the records
            records.stream()
                    .sorted(Comparator.comparing(record -> (String) record.getOrDefault("type", ""))) // Default to empty string if "type" is missing
                    .forEach(record -> {
                        int recordId = (Integer) record.getOrDefault("id", 0);
                        LocalDate startDate = (LocalDate) record.getOrDefault("start_date", LocalDate.now());
                        LocalDate endDate = (LocalDate) record.getOrDefault("end_date", LocalDate.now());
                        BigDecimal amount = (BigDecimal) record.getOrDefault("amount", BigDecimal.ZERO);
                        String type = (String) record.getOrDefault("type", "Unknown");
                        BigDecimal impactValue = (BigDecimal) record.getOrDefault("impact_value", BigDecimal.ZERO);

                        System.out.printf("Record ID: %d, Type: %s, Start Date: %s, End Date: %s, Amount: %s, Impact Value: %s%n",
                                recordId, type, startDate, endDate, amount, impactValue);
                    });
        } catch (SQLException e) {
            System.out.println("Error retrieving carbon records: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void generateCarbonConsumptionReport(Scanner scanner) {
        System.out.println("\n=== Generate Carbon Consumption Report ===");

        int userId = getValidIntegerInput(scanner, "Enter User ID for the report: ");

        System.out.println("Choose report type (1 for daily, 2 for weekly, 3 for monthly): ");
        int periodType = getValidIntegerInput(scanner, "Enter report type: ");

        if (periodType < 1 || periodType > 3) {
            System.out.println("Invalid report type. Please enter 1 for daily, 2 for weekly, or 3 for monthly.");
            return;
        }

        LocalDate startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");

        LocalDate endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");

        try {

            userService.generateConsumptionReport(userId, periodType, startDate, endDate);

        } catch (SQLException e) {
            System.out.println("Error generating the report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*CARBON management ends here*/

}