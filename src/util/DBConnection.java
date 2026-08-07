package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides shared access to the SQLite database connection.
 */
public class DBConnection {
    private static final String URL = "jdbc:sqlite:ecommerce.db";
    private static Connection connection = null;

    /**
     * Returns a shared database connection, creating it when needed.
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
                System.out.println("Database connected successfully!");
            } catch (SQLException e) {
                System.out.println("Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Closes the shared database connection if it is open.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}