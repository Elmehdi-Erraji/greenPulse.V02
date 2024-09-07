package views;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import entities.*;
import service.CarbonRecordService;
import services.UserService;

public class Main {
    private static int userIdCounter = 1000;
    private static int carbonRecordIdCounter = 1000;

    public static void main(String[] args) {
        try {
            UserService userService = new UserService();
            CarbonRecordService carbonRecordService = new CarbonRecordService();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nMenu");
                System.out.println("1. User Management");
                System.out.println("2. Carbon Record Management");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int mainChoice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (mainChoice) {
                    case 1:
                        userManagement(scanner, userService);
                        break;

                    case 2:
                        carbonRecordManagement(scanner, carbonRecordService);
                        break;

                    case 3:
                        // Exit
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void userManagement(Scanner scanner, UserService userService) throws SQLException {
        System.out.println("\nUser Management");
        System.out.println("1. Create User");
        System.out.println("2. Read User");
        System.out.println("3. Update User");
        System.out.println("4. Delete User");
        System.out.println("5. List All Users");
        System.out.println("6. Back to Main Menu");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        switch (choice) {
            case 1:
                // Create User
                System.out.print("Enter user name: ");
                String name = scanner.nextLine();
                System.out.print("Enter user age: ");
                int age = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                User newUser = new User(userIdCounter++, name, age);
                userService.createUser(newUser);
                System.out.println("User created: " + newUser);
                break;

            case 2:
                // Read User
                System.out.print("Enter user ID: ");
                int userId = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                User user = userService.getUserById(userId);
                if (user != null) {
                    System.out.println("User found: " + user);
                } else {
                    System.out.println("User not found.");
                }
                break;

            case 3:
                // Update User
                System.out.print("Enter user ID to update: ");
                int updateId = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                User userToUpdate = userService.getUserById(updateId);
                if (userToUpdate != null) {
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new age: ");
                    int newAge = scanner.nextInt();
                    scanner.nextLine();  // Consume newline
                    userToUpdate.setName(newName);
                    userToUpdate.setAge(newAge);
                    userService.updateUser(userToUpdate);
                    System.out.println("User updated: " + userToUpdate);
                } else {
                    System.out.println("User not found.");
                }
                break;

            case 4:
                // Delete User
                System.out.print("Enter user ID to delete: ");
                int deleteId = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                userService.deleteUser(deleteId);
                System.out.println("User deleted.");
                break;

            case 5:
                // List All Users
                List<User> users = userService.getAllUsers();
                System.out.println("All users:");
                for (User u : users) {
                    System.out.println(u);
                }
                break;

            case 6:
                // Back to Main Menu
                return;

            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private static void carbonRecordManagement(Scanner scanner, CarbonRecordService carbonRecordService) throws SQLException {
        System.out.println("\nCarbon Record Management");
        System.out.println("1. Create Carbon Record");
        System.out.println("2. Read Carbon Record");
        System.out.println("3. Update Carbon Record");
        System.out.println("4. Delete Carbon Record");
        System.out.println("5. List All Carbon Records");
        System.out.println("6. Back to Main Menu");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        switch (choice) {
            case 1:
                // Create Carbon Record
                System.out.print("Enter record type (Transport/Logement/Alimentation): ");
                String type = scanner.nextLine();
                System.out.print("Enter start date (YYYY-MM-DD): ");
                LocalDate startDate = LocalDate.parse(scanner.nextLine());
                System.out.print("Enter end date (YYYY-MM-DD): ");
                LocalDate endDate = LocalDate.parse(scanner.nextLine());
                System.out.print("Enter amount: ");
                double amount = scanner.nextDouble();
                scanner.nextLine();  // Consume newline

                CarbonRecord newRecord = null;
                if ("Transport".equalsIgnoreCase(type)) {
                    System.out.print("Enter distance: ");
                    double distance = scanner.nextDouble();
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter vehicle type: ");
                    String vehicleType = scanner.nextLine();
                    System.out.print("Enter user ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();  // Consume newline
                    newRecord = new Transport(startDate, endDate, amount, distance, vehicleType, userId);
                } else if ("Logement".equalsIgnoreCase(type)) {
                    System.out.print("Enter energy consumption: ");
                    double energyConsumption = scanner.nextDouble();
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter energy type: ");
                    String energyType = scanner.nextLine();
                    System.out.print("Enter user ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();  // Consume newline
                    newRecord = new Logement(startDate, endDate, amount, energyConsumption, energyType, userId);
                } else if ("Alimentation".equalsIgnoreCase(type)) {
                    System.out.print("Enter food type: ");
                    String foodType = scanner.nextLine();
                    System.out.print("Enter weight: ");
                    double weight = scanner.nextDouble();
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter user ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();  // Consume newline
                    newRecord = new Alimentation(startDate, endDate, amount, foodType, weight, userId);
                } else {
                    System.out.println("Invalid type.");
                    return;
                }

                newRecord.setId(carbonRecordIdCounter++);
                carbonRecordService.createCarbonRecord(newRecord);
                System.out.println("Carbon record created: " + newRecord);
                break;

            case 2:
                // Read Carbon Record
                System.out.print("Enter record ID: ");
                int recordId = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                CarbonRecord record = carbonRecordService.getCarbonRecordById(recordId);
                if (record != null) {
                    System.out.println("Carbon record found: " + record);
                } else {
                    System.out.println("Carbon record not found.");
                }
                break;

            case 3:
                // Update Carbon Record
                System.out.print("Enter record ID to update: ");
                int updateId = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                CarbonRecord recordToUpdate = carbonRecordService.getCarbonRecordById(updateId);
                if (recordToUpdate != null) {
                    System.out.print("Enter new start date (YYYY-MM-DD): ");
                    LocalDate newStartDate = LocalDate.parse(scanner.nextLine());
                    System.out.print("Enter new end date (YYYY-MM-DD): ");
                    LocalDate newEndDate = LocalDate.parse(scanner.nextLine());
                    System.out.print("Enter new amount: ");
                    double newAmount = scanner.nextDouble();
                    scanner.nextLine();  // Consume newline

                    recordToUpdate.setStartDate(newStartDate);
                    recordToUpdate.setEndDate(newEndDate);
                    recordToUpdate.setAmount(newAmount);

                    // Depending on the record type, update specific fields
                    if (recordToUpdate instanceof Transport) {
                        Transport transport = (Transport) recordToUpdate;
                        System.out.print("Enter new distance: ");
                        double newDistance = scanner.nextDouble();
                        scanner.nextLine();  // Consume newline
                        System.out.print("Enter new vehicle type: ");
                        String newVehicleType = scanner.nextLine();
                        transport.setDistance(newDistance);
                        transport.setVehicleType(newVehicleType);
                    } else if (recordToUpdate instanceof Logement) {
                        Logement logement = (Logement) recordToUpdate;
                        System.out.print("Enter new energy consumption: ");
                        double newEnergyConsumption = scanner.nextDouble();
                        scanner.nextLine();  // Consume newline
                        System.out.print("Enter new energy type: ");
                        String newEnergyType = scanner.nextLine();
                        logement.setEnergyConsumption(newEnergyConsumption);
                        logement.setEnergyType(newEnergyType);
                    } else if (recordToUpdate instanceof Alimentation) {
                        Alimentation alimentation = (Alimentation) recordToUpdate;
                        System.out.print("Enter new food type: ");
                        String newFoodType = scanner.nextLine();
                        System.out.print("Enter new weight: ");
                        double newWeight = scanner.nextDouble();
                        scanner.nextLine();  // Consume newline
                        alimentation.setFoodType(newFoodType);
                        alimentation.setWeight(newWeight);
                    }

                    carbonRecordService.updateCarbonRecord(recordToUpdate);
                    System.out.println("Carbon record updated: " + recordToUpdate);
                } else {
                    System.out.println("Carbon record not found.");
                }
                break;

            case 4:
                // Delete Carbon Record
                System.out.print("Enter record ID to delete: ");
                int deleteId = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                carbonRecordService.deleteCarbonRecord(deleteId);
                System.out.println("Carbon record deleted.");
                break;

            case 5:
                // List All Carbon Records
                List<CarbonRecord> records = carbonRecordService.getAllCarbonRecords();
                System.out.println("All carbon records:");
                for (CarbonRecord r : records) {
                    System.out.println(r);
                }
                break;

            case 6:
                // Back to Main Menu
                return;

            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }
}
