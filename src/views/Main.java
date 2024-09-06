package views;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import entities.User;
import services.UserService;

public class Main {
    private static int userIdCounter = 1000;

    public static void main(String[] args) {
        try {
            UserService userService = new UserService();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nUser Management System");
                System.out.println("1. Create User");
                System.out.println("2. Read User");
                System.out.println("3. Update User");
                System.out.println("4. Delete User");
                System.out.println("5. List All Users");
                System.out.println("6. Exit");
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
                        User newUser = new User(userIdCounter++,name, age);
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
}
