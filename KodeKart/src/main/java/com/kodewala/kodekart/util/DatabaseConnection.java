package com.kodewala.kodekart.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String URL = "jdbc:mysql://localhost:3306/kodekart";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Hackerbubai007#";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Registered Successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + URL);
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // For JUnit testing
    public static void useTestDatabase() {
        URL = "jdbc:mysql://localhost:3306/kodekart_test";
        System.out.println("Switched to test database: kodekart_test");
    }
    
    public static void useMainDatabase() {
        URL = "jdbc:mysql://localhost:3306/kodekart";
        System.out.println("Switched back to main database: kodekart");
    }
}