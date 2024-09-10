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
            CarbonRecordRepository carbonRecordRepository = new CarbonRecordRepository(connection);

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
            System.out.println("4. Go Back");

            int choice = getValidIntegerInput(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    addCarbonRecord(scanner);
                    break;
                case 2:
                    // Implement viewCarbonRecords();
                    break;
                case 3:
                    // Implement deleteCarbonRecord(scanner);
                    break;
                case 4:
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
        System.out.println("\n=== Users ===");
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

    private static void addCarbonRecord(Scanner scanner) throws SQLException {
        int userId = getValidIntegerInput(scanner, "Enter user ID: ");

        if (!userService.isUserExist(userId)) {
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
        LocalDate startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");
        LocalDate endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");
        BigDecimal amount = getValidBigDecimalInput(scanner, "Enter amount: ");
        double energyConsumption = getValidDoubleInput(scanner, "Enter energy consumption: ");
        EnergyType energyType = getEnergyTypeFromInput(scanner);

        Logement logement = new Logement(startDate, endDate, amount, TypeConsommation.LOGEMENT, userId, energyConsumption, energyType);
        carbonRecordService.addLogementRecord(logement);
        System.out.println("Logement carbon record added successfully!");
    }

    private static void addTransportRecord(Scanner scanner, int userId) throws SQLException {
        LocalDate startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");
        LocalDate endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");
        BigDecimal amount = getValidBigDecimalInput(scanner, "Enter amount: ");
        double distance = getValidDoubleInput(scanner, "Enter distance: ");
        VehicleType vehicleType = getVehicleTypeFromInput(scanner);

        Transport transport = new Transport(startDate, endDate, amount, TypeConsommation.TRANSPORT, userId, distance, vehicleType);
        carbonRecordService.addTransportRecord(transport);
        System.out.println("Transport carbon record added successfully!");
    }

    private static void addAlimentationRecord(Scanner scanner, int userId) throws SQLException {
        LocalDate startDate = getValidDateInput(scanner, "Enter start date (yyyy-MM-dd): ");
        LocalDate endDate = getValidDateInput(scanner, "Enter end date (yyyy-MM-dd): ");
        BigDecimal amount = getValidBigDecimalInput(scanner, "Enter amount: ");
        double foodConsumption = getValidDoubleInput(scanner, "Enter food consumption: ");
        double foodWeight = getValidDoubleInput(scanner, "Enter food weight: ");
        FoodType foodType = getFoodTypeFromInput(scanner);

        Alimentation alimentation = new Alimentation(startDate, endDate, amount, TypeConsommation.ALIMENTATION, userId, foodConsumption, foodType, foodWeight);
        carbonRecordService.addAlimentationRecord(alimentation);
        System.out.println("Alimentation carbon record added successfully!");
    }

    private static TypeConsommation getTypeConsommationFromInput(Scanner scanner) {
        System.out.println("Select consumption type:");
        TypeConsommation[] types = TypeConsommation.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].name());
        }

        int choice = getValidIntegerInput(scanner, "Enter the number corresponding to the consumption type: ");
        if (choice < 1 || choice > types.length) {
            System.out.println("Invalid choice. Please try again.");
            return getTypeConsommationFromInput(scanner); // Recursive retry
        }
        return types[choice - 1];
    }

    private static EnergyType getEnergyTypeFromInput(Scanner scanner) {
        System.out.println("Select energy type:");
        EnergyType[] energyTypes = EnergyType.values();
        for (int i = 0; i < energyTypes.length; i++) {
            System.out.println((i + 1) + ". " + energyTypes[i].name());
        }

        int choice = getValidIntegerInput(scanner, "Enter the number corresponding to the energy type: ");
        if (choice < 1 || choice > energyTypes.length) {
            System.out.println("Invalid choice. Please try again.");
            return getEnergyTypeFromInput(scanner); // Recursive retry
        }
        return energyTypes[choice - 1];
    }

    private static VehicleType getVehicleTypeFromInput(Scanner scanner) {
        System.out.println("Select vehicle type:");
        VehicleType[] vehicleTypes = VehicleType.values();
        for (int i = 0; i < vehicleTypes.length; i++) {
            System.out.println((i + 1) + ". " + vehicleTypes[i].name());
        }

        int choice = getValidIntegerInput(scanner, "Enter the number corresponding to the vehicle type: ");
        if (choice < 1 || choice > vehicleTypes.length) {
            System.out.println("Invalid choice. Please try again.");
            return getVehicleTypeFromInput(scanner); // Recursive retry
        }
        return vehicleTypes[choice - 1];
    }

    private static FoodType getFoodTypeFromInput(Scanner scanner) {
        System.out.println("Select food type:");
        FoodType[] foodTypes = FoodType.values();
        for (int i = 0; i < foodTypes.length; i++) {
            System.out.println((i + 1) + ". " + foodTypes[i].name());
        }

        int choice = getValidIntegerInput(scanner, "Enter the number corresponding to the food type: ");
        if (choice < 1 || choice > foodTypes.length) {
            System.out.println("Invalid choice. Please try again.");
            return getFoodTypeFromInput(scanner); // Recursive retry
        }
        return foodTypes[choice - 1];
    }

    private static LocalDate getValidDateInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please try again.");
            }
        }
    }

    private static int getValidIntegerInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static BigDecimal getValidBigDecimalInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid decimal number.");
            }
        }
    }

    private static double getValidDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
