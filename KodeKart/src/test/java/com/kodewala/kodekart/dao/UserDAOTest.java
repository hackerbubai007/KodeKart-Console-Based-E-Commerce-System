package com.kodewala.kodekart.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kodewala.kodekart.bean.User;
import com.kodewala.kodekart.util.DatabaseConnection;

class UserDAOTest {
    
    private static Connection connection;
    private UserDAO userDAO;

    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseConnection.useTestDatabase();
        connection = DatabaseConnection.getConnection();
        assertNotNull(connection, "Database connection should not be null");

        try (Statement stmt = connection.createStatement()) {
            // Drop and recreate to ensure clean state
            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute("""
                CREATE TABLE users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    phone VARCHAR(15),
                    password VARCHAR(100) NOT NULL,
                    is_admin BOOLEAN DEFAULT FALSE
                )
            """);
        }
        System.out.println(" Test database setup completed.");
    }

    @BeforeEach
    void init() {
        userDAO = new UserDAO();
        // Clean the table before each test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println(" Database connection closed.");
        }
        // Switch back to main database
        DatabaseConnection.useMainDatabase();
    }

    @Test
    void testRegisterUser_Success() {
        User user = new User("dipankar", "dip@kodewala.com", "9999999999", "pass123");
        boolean result = userDAO.registerUser(user);
        assertTrue(result, "User should register successfully");
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        User user1 = new User("aswini", "soumadip@gmail.com", "8888888888", "abc123");
        User user2 = new User("soumadip", "soumadip@gmail.com", "7777777777", "xyz123");

        boolean firstRegistration = userDAO.registerUser(user1);
        assertTrue(firstRegistration, "First registration should succeed");
        
        boolean secondRegistration = userDAO.registerUser(user2); // same email
        assertFalse(secondRegistration, "Duplicate email should not be allowed");
    }

    @Test
    void testEmailExists() {
        User user = new User("Riya", "riya@google.com", "9090909090", "test123");
        boolean registered = userDAO.registerUser(user);
        assertTrue(registered, "User should be registered first");

        assertTrue(userDAO.emailExists("riya@google.com"), "Email should exist after registration");
        assertFalse(userDAO.emailExists("Yogesh@hsbc.com"), "Non-existent email should return false");
    }

    @Test
    void testLoginUser_SuccessAndFail() {
        User user = new User("Karan", "karan@example.com", "8080808080", "secure123");
        boolean registered = userDAO.registerUser(user);
        assertTrue(registered, "User should be registered first");

        // Valid login
        User loggedInUser = userDAO.loginUser("karan@example.com", "secure123");
        assertNotNull(loggedInUser, "Valid login should succeed");
        assertEquals("Karan", loggedInUser.getuName());

        // Wrong password
        assertNull(userDAO.loginUser("karan@example.com", "wrongpass"), "Wrong password should fail");
        
        // Unknown email
        assertNull(userDAO.loginUser("unknown@example.com", "secure123"), "Unknown email should fail");
    }
}