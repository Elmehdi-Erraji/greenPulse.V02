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

public class Main {

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
            System.out.println("4. Go Back");

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
        scanner.nextLine(); // Consume newline

        List<User> filteredUsers = userService.filterUsersByTotalConsumption(threshold);

        System.out.println("\nUsers with consumption exceeding " + threshold + " KgCO2eq:");
        for (User user : filteredUsers) {
            System.out.printf("User ID: %d, Name: %s, Total Consumption: %.2f KgCO2eq\n",
                    user.getId(), user.getName(), user.getCarbonRecords().stream()
                            .mapToDouble(record -> record.getAmount().doubleValue()) // Convert BigDecimal to double
                            .sum());
        }
    }


    /*USER management ends here*/


    /*CARBON management starts here*/


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
            try {
                carbonRecordService.deleteCarbonRecord(recordId);
                System.out.println("Carbon record deleted successfully.");
            } catch (SQLException e) {
                System.out.println("Error deleting carbon record: " + e.getMessage());
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    private static void viewCarbonRecords(Scanner scanner) throws SQLException {
        System.out.println("\n=== View Carbon Records ===");

        int userId = getValidIntegerInput(scanner, "Enter user ID: ");

        try {
            List<Map<String, Object>> records = carbonRecordService.getAllRecordsByUserId(userId);
            if (records.isEmpty()) {
                System.out.println("No carbon records found for this user.");
                return;
            }

            records.stream()
                    .sorted(Comparator.comparing(record -> (String) record.get("type")))
                    .forEach(record -> {
                        int recordId = (Integer) record.get("id");
                        LocalDate startDate = (LocalDate) record.get("start_date");
                        LocalDate endDate = (LocalDate) record.get("end_date");
                        BigDecimal amount = (BigDecimal) record.get("amount");
                        String type = (String) record.get("type");
                        BigDecimal impactValue = (BigDecimal) record.get("impact_value");

                        System.out.printf("Record ID: %d, Type: %s, Start Date: %s, End Date: %s, Amount: %s, Impact Value: %s\n",
                                recordId, type, startDate, endDate, amount, impactValue);
                    });
        } catch (SQLException e) {
            System.out.println("Error retrieving carbon records: " + e.getMessage());
        }
    }

    /*CARBON management ends here*/





















   /* Validation methods start*/
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
    private static TypeConsommation getTypeConsommationFromInput(Scanner scanner) {
        System.out.println("Select consumption type:");
        TypeConsommation[] types = TypeConsommation.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].name());
        }

        int choice = getValidIntegerInput(scanner, "Enter the number corresponding to the consumption type: ");
        if (choice < 1 || choice > types.length) {
            System.out.println("Invalid choice. Please try again.");
            return getTypeConsommationFromInput(scanner);
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
    /* Validation methods end*/

}