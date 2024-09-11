package validation;

import entities.enums.EnergyType;
import entities.enums.FoodType;
import entities.enums.TypeConsommation;
import entities.enums.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class InputValidator {

    public static double getValidDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    public static BigDecimal getValidBigDecimalInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid decimal number.");
            }
        }
    }
    public static LocalDate getValidDateInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please try again.");
            }
        }
    }
    public static int getValidIntegerInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    public static TypeConsommation getTypeConsommationFromInput(Scanner scanner) {
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
    public static EnergyType getEnergyTypeFromInput(Scanner scanner) {
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
    public static VehicleType getVehicleTypeFromInput(Scanner scanner) {
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
    public static FoodType getFoodTypeFromInput(Scanner scanner) {
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
}
