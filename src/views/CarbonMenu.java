package views;

import entities.Alimentation;
import entities.Logement;
import entities.Transport;
import entities.enums.EnergyType;
import entities.enums.FoodType;
import entities.enums.TypeConsommation;
import entities.enums.VehicleType;
import services.CarbonRecordService;
import services.UserService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static validation.InputValidator.*;
import static validation.InputValidator.getValidDateInput;

public class CarbonMenu {
    private static CarbonRecordService carbonRecordService;
    private static UserService userService;

    public CarbonMenu(CarbonRecordService carbonRecordService, UserService userService) {
        this.carbonRecordService = carbonRecordService;
        this.userService = userService;
    }


    public void displayCarbonRecordManagementMenu(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n=== Carbon Record Management Menu ===");
            System.out.println("1. Add Carbon Record");
            System.out.println("2. View Carbon Records");
            System.out.println("3. Delete Carbon Record");
            System.out.println("4. Generate Consumption Report");
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
                    generateCarbonConsumptionReport(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
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
    }}
