package services;

import entities.User;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserRepository userRepository;

    public UserService(Connection connection) {
        this.userRepository = new UserRepository(connection);
    }

    public void createUser(User user) throws SQLException {
        userRepository.createUser(user);
    }

    public User getUserById(int id) throws SQLException {
        return userRepository.getUserById(id);
    }

    public List<User> getAllUsers() throws SQLException {
        return userRepository.getAllUsers();
    }

    public void updateUser(User user) throws SQLException {
        userRepository.updateUser(user);
    }

    public void deleteUser(int id) throws SQLException {
        userRepository.deleteUser(id);
    }
}
