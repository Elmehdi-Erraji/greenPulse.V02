package views;

import java.sql.SQLException;
import java.util.List;
import services.UserService;
import entities.User;

public class Main {

    public static void main(String[] args) {
        try {
            UserService userService = new UserService();

            // Create a new user
            User newUser = new User(1,"John Doe", 30);
            userService.createUser(newUser);
            System.out.println("User created: " + newUser);

            // Read a user by ID
            User user = userService.getUserById(1); // Example ID
            System.out.println("User fetched: " + user);

//            // Update a user
//            if (user != null) {
//                user.setName("Jane Doe");
//                userService.updateUser(user);
//                System.out.println("User updated: " + userService.getUserById(1));
//            }

            // List all users
            List<User> users = userService.getAllUsers();
            System.out.println("All users:");
            for (User u : users) {
                System.out.println(u);
            }

//            // Delete a user
//            userService.deleteUser(1); // Example ID
//            System.out.println("User deleted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
