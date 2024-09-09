package views;

import entities.*;
import entities.enums.TypeConsommation;
import repository.CarbonRecordRepository;
import services.CarbonRecordService;
import services.UserService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    private static UserService userService;
    private static CarbonRecordService carbonRecordService;

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GreenPulse", "GreenPulse", "");
             Scanner scanner = new Scanner(System.in)) {

            userService = new UserService(connection);

            carbonRecordService = new CarbonRecordService(connection);
            while (true) {
                System.out.println("Main Menu:");
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
                        return; // Exit the program
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getValidIntegerInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                return input;
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine(); // Discard invalid input
            }
        }
    }

    private static LocalDate getValidDateInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
            }
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

            int choice = getValidIntegerInput(scanner, "Enter your choice: ");

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

            int choice = getValidIntegerInput(scanner, "Enter your choice: ");

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
        int age = getValidIntegerInput(scanner, "Enter user age: ");

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

    private static void addCarbonRecord(Scanner scanner, UserService userService, CarbonRecordService carbonRecordService) throws SQLException {
        LocalDate startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");
        LocalDate endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");
        System.out.print("Enter amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        System.out.println("Enter type:");
        for (TypeConsommation type : TypeConsommation.values()) {
            System.out.println("- " + type);
        }
        TypeConsommation type = TypeConsommation.valueOf(scanner.nextLine().toUpperCase());
        int userId = getValidIntegerInput(scanner, "Enter user ID: ");

        if (!userService.isUserExist(userId)) {
            System.out.println("User ID does not exist. Please enter a valid User ID.");
            return;
        }

        CarbonRecord record = createCarbonRecordFromInput(scanner, type, startDate, endDate, amount, userId);
        carbonRecordService.createCarbonRecord(record);
        System.out.println("Carbon record added successfully!");
    }

    private static CarbonRecord createCarbonRecordFromInput(Scanner scanner, TypeConsommation type, LocalDate startDate, LocalDate endDate, BigDecimal amount, int userId) {
        switch (type) {
            case TRANSPORT:
                System.out.print("Enter distance: ");
                double distance = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter vehicle type: ");
                String vehicleType = scanner.nextLine();
                return new Transport(startDate, endDate, amount, type, userId, distance, vehicleType);
            case LOGEMENT:
                System.out.print("Enter energy consumption: ");
                double energyConsumption = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter energy type: ");
                String energyType = scanner.nextLine();
                return new Logement(startDate, endDate, amount, type, userId, energyConsumption, energyType);
            case ALIMENTATION:
                System.out.print("Enter food consumption: ");
                double foodConsumption = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter food type: ");
                String foodType = scanner.nextLine();
                return new Alimentation(startDate, endDate, amount, type, userId, foodConsumption, foodType);
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }

    private static void viewCarbonRecords() throws SQLException {
        System.out.println("Carbon Records:");
        for (CarbonRecord record : carbonRecordService.getAllCarbonRecords()) {
            System.out.println(record);
        }
    }

    private static void updateCarbonRecord(Scanner scanner) throws SQLException {
        int id = getValidIntegerInput(scanner, "Enter record ID to update: ");

        LocalDate startDate = getValidDateInput(scanner, "Enter new start date (yyyy-MM-dd): ");
        LocalDate endDate = getValidDateInput(scanner, "Enter new end date (yyyy-MM-dd): ");
        System.out.print("Enter new amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        System.out.println("Enter new type:");
        for (TypeConsommation type : TypeConsommation.values()) {
            System.out.println("- " + type);
        }
        TypeConsommation type = TypeConsommation.valueOf(scanner.nextLine().toUpperCase());
        int userId = getValidIntegerInput(scanner, "Enter new user ID: ");

        CarbonRecord record = createCarbonRecordFromInput(scanner, type, startDate, endDate, amount, userId);
        record.setId(id); // Set the ID of the record to update
        carbonRecordService.updateCarbonRecord(record);

        System.out.println("Carbon record updated successfully!");
    }

    private static void deleteCarbonRecord(Scanner scanner) throws SQLException {
        int id = getValidIntegerInput(scanner, "Enter record ID to delete: ");

        carbonRecordService.deleteCarbonRecord(id);

        System.out.println("Carbon record deleted successfully!");
    }
}
