package views;

import entities.*;
import entities.enums.TypeConsommation;
import services.CarbonRecordService;
import services.UserService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static UserService userService;
    private static CarbonRecordService carbonRecordService;

    public static void main(String[] args) {
        try {
            // Initialize the database connection
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GreenPulse", "GreenPulse", "");
            userService = new UserService(connection);
            carbonRecordService = new CarbonRecordService(connection);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Main Menu:");
                System.out.println("1. User Management");
                System.out.println("2. Carbon Record Management");
                System.out.println("3. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        userManagementMenu(scanner);
                        break;
                    case 2:
                        carbonRecordManagementMenu(scanner);
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        connection.close();
                        System.exit(0);
                        break;
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
            System.out.println("User Management Menu:");
            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Go Back");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addUser(scanner);
                    break;
                case 2:
                    viewUsers();
                    break;
                case 3:
                    updateUser(scanner);
                    break;
                case 4:
                    deleteUser(scanner);
                    break;
                case 5:
                    return; // Go back to the main menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void carbonRecordManagementMenu(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("Carbon Record Management Menu:");
            System.out.println("1. Add Carbon Record");
            System.out.println("2. View Carbon Records");
            System.out.println("3. Update Carbon Record");
            System.out.println("4. Delete Carbon Record");
            System.out.println("5. Go Back");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addCarbonRecord(scanner, userService, carbonRecordService);
                    break;
                case 2:
                    viewCarbonRecords();
                    break;
                case 3:
                    updateCarbonRecord(scanner);
                    break;
                case 4:
                    deleteCarbonRecord(scanner);
                    break;
                case 5:
                    return; // Go back to the main menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }



    private static void addUser(Scanner scanner) throws SQLException {
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();
        System.out.print("Enter user age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        User user = new User(0, name, age);  // ID will be auto-generated by the database
        userService.createUser(user);

        System.out.println("User added successfully!");
    }

    private static void viewUsers() throws SQLException {
        System.out.println("Users:");
        for (User user : userService.getAllUsers()) {
            System.out.println(user);
        }
    }

    private static void updateUser(Scanner scanner) throws SQLException {
        System.out.print("Enter user ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new user name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new user age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        User user = new User(id, name, age);
        userService.updateUser(user);

        System.out.println("User updated successfully!");
    }

    private static void deleteUser(Scanner scanner) throws SQLException {
        System.out.print("Enter user ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        userService.deleteUser(id);

        System.out.println("User deleted successfully!");
    }

    private static void addCarbonRecord(Scanner scanner, UserService userService, CarbonRecordService carbonRecordService) throws SQLException {
        // Prompt for and read the start date
        System.out.print("Enter start date (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        // Prompt for and read the end date
        System.out.print("Enter end date (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        // Prompt for and read the amount
        System.out.print("Enter amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        // Prompt for and read the type
        System.out.print("Enter type (TRANSPORT, LOGEMENT, ALIMENTATION): ");
        String typeStr = scanner.nextLine();
        TypeConsommation type = TypeConsommation.valueOf(typeStr.toUpperCase());

        // Prompt for and read the user ID
        System.out.print("Enter user ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        // Check if the user exists
        if (userService.getUserById(userId) == null) {
            System.out.println("User with ID " + userId + " does not exist.");
            return;
        }

        // Create the appropriate CarbonRecord based on the type
        CarbonRecord record;
        switch (type) {
            case TRANSPORT:
                System.out.print("Enter distance: ");
                double distance = scanner.nextDouble();
                scanner.nextLine(); // Consume the newline character
                System.out.print("Enter vehicle type: ");
                String vehicleType = scanner.nextLine();
                record = new Transport(startDate, endDate, amount, type, userId, distance, vehicleType);
                break;
            case LOGEMENT:
                System.out.print("Enter energy consumption: ");
                double energyConsumption = scanner.nextDouble();
                scanner.nextLine(); // Consume the newline character
                System.out.print("Enter energy type: ");
                String energyType = scanner.nextLine();
                record = new Logement(startDate, endDate, amount, type, userId, energyConsumption, energyType);
                break;
            case ALIMENTATION:
                // Since ALIMENTATION type does not require extra fields, just create a new instance
                record = new Alimentation(startDate, endDate, amount, type, userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown type");
        }

        // Add the carbon record using the CarbonRecordService
        carbonRecordService.createCarbonRecord(record);
        System.out.println("Carbon record added successfully!");
    }

    private static void viewCarbonRecords() throws SQLException {
        System.out.println("Carbon Records:");
        for (CarbonRecord record : carbonRecordService.getAllCarbonRecords()) {
            System.out.println(record);
        }
    }

    private static void updateCarbonRecord(Scanner scanner) throws SQLException {
        System.out.print("Enter record ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new start date (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter new end date (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter new amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        System.out.print("Enter new type (TRANSPORT, LOGEMENT, ALIMENTATION): ");
        String typeStr = scanner.nextLine();
        TypeConsommation type = TypeConsommation.valueOf(typeStr.toUpperCase());
        System.out.print("Enter new user ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        CarbonRecord record;
        switch (type) {
            case TRANSPORT:
                System.out.print("Enter new distance: ");
                double distance = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter new vehicle type: ");
                String vehicleType = scanner.nextLine();
                record = new Transport(startDate, endDate, amount, type, userId, distance, vehicleType);
                break;
            case LOGEMENT:
                System.out.print("Enter new energy consumption: ");
                double energyConsumption = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter new energy type: ");
                String energyType = scanner.nextLine();
                record = new Logement(startDate, endDate, amount, type, userId, energyConsumption, energyType);
                break;
            case ALIMENTATION:
                record = new Alimentation(startDate, endDate, amount, type, userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown type");
        }

        record.setId(id); // Set the ID of the record to update
        carbonRecordService.updateCarbonRecord(record);

        System.out.println("Carbon record updated successfully!");
    }

    private static void deleteCarbonRecord(Scanner scanner) throws SQLException {
        System.out.print("Enter record ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        carbonRecordService.deleteCarbonRecord(id);

        System.out.println("Carbon record deleted successfully!");
    }
}
