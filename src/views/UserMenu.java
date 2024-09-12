package views;

import entities.*;
import services.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static validation.InputValidator.getValidDateInput;
import static validation.InputValidator.getValidIntegerInput;

public class UserMenu {
    private static UserService userService;

    public UserMenu(UserService userService) {
        this.userService = userService;
    }

    public void displayUserManagementMenu(Scanner scanner) throws SQLException {
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

        List<User> filteredUsers = userService.filterUsersByTotalConsumption(threshold);

        System.out.println("\nUsers with consumption exceeding " + threshold + " KgCO2eq:");
        for (User user : filteredUsers) {
            System.out.printf("User ID: %d, Name: %s, Total Consumption: %.2f KgCO2eq\n",
                    user.getId(), user.getName(), user.getCarbonRecords().stream()
                            .mapToDouble(record -> record.getAmount().doubleValue()) // Convert BigDecimal to double
                            .sum());
        }
    }


}
