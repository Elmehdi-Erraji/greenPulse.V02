package views;

import services.CarbonRecordService;
import services.UserService;

import java.sql.SQLException;
import java.util.Scanner;

import static validation.InputValidator.getValidIntegerInput;

public class MainMenu {
    private static UserService userService;
    private static CarbonRecordService carbonRecordService;

    public MainMenu(UserService userService, CarbonRecordService carbonRecordService) {
        this.userService = userService;
        this.carbonRecordService = carbonRecordService;
    }

    public static void displayMainMenu(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. User Management");
            System.out.println("2. Carbon Record Management");
            System.out.println("3. Exit");

            int choice = getValidIntegerInput(scanner, "Enter your choice: ");
            switch (choice) {
                case 1:
                    new UserMenu(userService).displayUserManagementMenu(scanner);
                    break;
                case 2:
                    new CarbonMenu(carbonRecordService, userService).displayCarbonRecordManagementMenu(scanner);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
