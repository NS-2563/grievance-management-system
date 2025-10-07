package com.grievance.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing and establishing database connections.
 * This uses the JDBC driver for MySQL.
 */
public class DBUtil {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // *** IMPORTANT: Change the following to match your local MySQL configuration ***
    private static final String DB_URL = "jdbc:mysql://localhost:3306/grievance_system?serverTimezone=UTC";

    // Database credentials
    private static final String USER = "root"; // <-- CHANGE THIS
    private static final String PASS = "Nagu@2563"; // <-- CHANGE THIS

    /**
     * Establishes a connection to the database.
     * @return Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Register JDBC driver (optional for modern JDBC, but good practice)
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Ensure the Connector/J JAR is in your build path.");
            throw new SQLException("JDBC Driver not found.", e);
        }

        System.out.println("Connecting to database...");
        // Establish the connection
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * Closes the database connection.
     * @param connection The connection to close.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing the database connection: " + e.getMessage());
            }
        }
    }
}
	