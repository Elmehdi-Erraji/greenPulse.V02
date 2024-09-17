package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/GreenPulse";
    private static final String USER = "GreenPulse";
    private static final String PASSWORD = "";

    private static final Database INSTANCE = new Database();

    private final Connection connection;

    private Database() {
        try {
            connection = DriverManager.getConnection(URL, USER , PASSWORD);
        }catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database");
        }
    }
    public static Database getInstance() {
        return INSTANCE;
    }
    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }





}
