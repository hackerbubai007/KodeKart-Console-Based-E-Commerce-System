package com.kodewala.kodekart.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kodewala.kodekart.bean.CartItems;
import com.kodewala.kodekart.util.DatabaseConnection;

class CartDAOTest {
    
    private static Connection connection;
    private CartDAO cartDAO;
    private ProductDAO productDAO;
    private static int testUserId = 1;
    private static int testProductId1;
    private static int testProductId2;

    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseConnection.useTestDatabase();
        connection = DatabaseConnection.getConnection();
        assertNotNull(connection, "Database connection should not be null");

        try (Statement stmt = connection.createStatement()) {
            // Drop tables in correct order to handle foreign key constraints
            stmt.execute("DROP TABLE IF EXISTS order_items");
            stmt.execute("DROP TABLE IF EXISTS orders");
            stmt.execute("DROP TABLE IF EXISTS cart");
            stmt.execute("DROP TABLE IF EXISTS products");
            stmt.execute("DROP TABLE IF EXISTS users");
            
            // Create users table
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
            
            // Create products table
            stmt.execute("""
                CREATE TABLE products (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    category VARCHAR(50),
                    price DECIMAL(10,2) NOT NULL,
                    quantity INT NOT NULL,
                    description TEXT
                )
            """);
            
            // Create cart table
            stmt.execute("""
                CREATE TABLE cart (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT,
                    product_id INT,
                    quantity INT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                )
            """);
            
            // Create orders table (for completeness, though not used in CartDAO)
            stmt.execute("""
                CREATE TABLE orders (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT,
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    total_amount DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
            
            // Create order_items table (for completeness, though not used in CartDAO)
            stmt.execute("""
                CREATE TABLE order_items (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT,
                    product_id INT,
                    quantity INT NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                )
            """);
            
            // Insert test user
            stmt.execute("INSERT INTO users (name, email, phone, password) VALUES ('Test User', 'test@example.com', '1234567890', 'password')");
            
            // Insert test products
            stmt.execute("INSERT INTO products (name, category, price, quantity, description) VALUES " +
                        "('Test Laptop', 'Electronics', 999.99, 10, 'Test laptop'), " +
                        "('Test Phone', 'Electronics', 499.99, 15, 'Test phone')");
        }
        System.out.println("Test database setup completed.");
    }

    @BeforeEach
    void init() {
        cartDAO = new CartDAO();
        productDAO = new ProductDAO();
        
        // Clean tables and reset data before each test
        try (Statement stmt = connection.createStatement()) {
            // Clear data in correct order to handle foreign key constraints
            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM orders");
            stmt.execute("DELETE FROM cart");
            stmt.execute("DELETE FROM products");
            stmt.execute("DELETE FROM users");
            
            // Reset auto-increment
            stmt.execute("ALTER TABLE order_items AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE orders AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE cart AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE products AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE users AUTO_INCREMENT = 1");
            
            // Insert test data
            stmt.execute("INSERT INTO users (name, email, phone, password) VALUES ('Test User', 'test@example.com', '1234567890', 'password')");
            stmt.execute("INSERT INTO products (name, category, price, quantity, description) VALUES " +
                        "('Test Laptop', 'Electronics', 999.99, 10, 'Test laptop'), " +
                        "('Test Phone', 'Electronics', 499.99, 15, 'Test phone')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Get test product IDs
        List<com.kodewala.kodekart.bean.Product> products = productDAO.getAllProducts();
        if (products.size() >= 2) {
            testProductId1 = products.get(0).getpId();
            testProductId2 = products.get(1).getpId();
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Clear only cart-related data after each test
            stmt.execute("DELETE FROM cart");
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            // Clean up all tables at the end
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS order_items");
                stmt.execute("DROP TABLE IF EXISTS orders");
                stmt.execute("DROP TABLE IF EXISTS cart");
                stmt.execute("DROP TABLE IF EXISTS products");
                stmt.execute("DROP TABLE IF EXISTS users");
            }
            connection.close();
            System.out.println("Database connection closed.");
        }
        // Switch back to main database
        DatabaseConnection.useMainDatabase();
    }

    @Test
    void testAddToCart_NewItem() {
        boolean result = cartDAO.addToCart(testUserId, testProductId1, 2);
        
        assertTrue(result, "Should add new item to cart successfully");
        
        // Verify the item was added
        List<CartItems> cartItems = cartDAO.getCartItems(testUserId);
        assertEquals(1, cartItems.size(), "Cart should have one item");
        assertEquals(testProductId1, cartItems.get(0).getProductId());
        assertEquals(2, cartItems.get(0).getQuantity());
    }

    @Test
    void testAddToCart_UpdateExistingItem() {
        // First add an item
        cartDAO.addToCart(testUserId, testProductId1, 2);
        
        // Add the same item again with different quantity
        boolean result = cartDAO.addToCart(testUserId, testProductId1, 3);
        
        assertTrue(result, "Should update existing item quantity");
        
        // Verify the quantity was updated (should be 2 + 3 = 5)
        List<CartItems> cartItems = cartDAO.getCartItems(testUserId);
        assertEquals(1, cartItems.size(), "Cart should still have one item");
        assertEquals(5, cartItems.get(0).getQuantity(), "Quantity should be updated to 5");
    }

    @Test
    void testAddToCart_MultipleItems() {
        // Add multiple different items
        cartDAO.addToCart(testUserId, testProductId1, 1);
        cartDAO.addToCart(testUserId, testProductId2, 2);
        
        List<CartItems> cartItems = cartDAO.getCartItems(testUserId);
        
        assertEquals(2, cartItems.size(), "Cart should have two items");
        
        // Verify both items are present
        boolean hasProduct1 = cartItems.stream().anyMatch(item -> item.getProductId() == testProductId1);
        boolean hasProduct2 = cartItems.stream().anyMatch(item -> item.getProductId() == testProductId2);
        
        assertTrue(hasProduct1, "Cart should contain product 1");
        assertTrue(hasProduct2, "Cart should contain product 2");
    }

    @Test
    void testGetCartItems_EmptyCart() {
        List<CartItems> cartItems = cartDAO.getCartItems(testUserId);
        
        assertNotNull(cartItems, "Cart items list should not be null");
        assertTrue(cartItems.isEmpty(), "Should return empty list for empty cart");
    }

    @Test
    void testGetCartItems_WithProductDetails() {
        // Add item to cart
        cartDAO.addToCart(testUserId, testProductId1, 1);
        
        List<CartItems> cartItems = cartDAO.getCartItems(testUserId);
        
        assertFalse(cartItems.isEmpty(), "Cart should have items");
        
        CartItems item = cartItems.get(0);
        assertNotNull(item.getProduct(), "Cart item should have product details");
        assertEquals("Test Laptop", item.getProduct().getpName());
        assertEquals(999.99, item.getProduct().getpPrice(), 0.01);
        assertEquals("Electronics", item.getProduct().getpCategory());
        assertEquals("Test laptop", item.getProduct().getpDescription());
    }

    @Test
    void testRemoveFromCart_Success() {
        // First add an item
        cartDAO.addToCart(testUserId, testProductId1, 1);
        
        // Get the cart item ID
        List<CartItems> cartItems = cartDAO.getCartItems(testUserId);
        int cartItemId = cartItems.get(0).getcId();
        
        // Remove the item
        boolean result = cartDAO.removeFromCart(cartItemId);
        
        assertTrue(result, "Should remove item from cart successfully");
        
        // Verify cart is empty
        List<CartItems> updatedCartItems = cartDAO.getCartItems(testUserId);
        assertTrue(updatedCartItems.isEmpty(), "Cart should be empty after removal");
    }

    @Test
    void testRemoveFromCart_NotFound() {
        boolean result = cartDAO.removeFromCart(999); // Non-existent cart item ID
        
        assertFalse(result, "Should return false for non-existent cart item");
    }

    @Test
    void testClearCart_Success() {
        // Add multiple items
        cartDAO.addToCart(testUserId, testProductId1, 1);
        cartDAO.addToCart(testUserId, testProductId2, 2);
        
        // Verify cart has items
        List<CartItems> cartItemsBefore = cartDAO.getCartItems(testUserId);
        assertEquals(2, cartItemsBefore.size(), "Cart should have items before clear");
        
        // Clear cart
        boolean result = cartDAO.clearCart(testUserId);
        
        assertTrue(result, "Should clear cart successfully");
        
        // Verify cart is empty
        List<CartItems> cartItemsAfter = cartDAO.getCartItems(testUserId);
        assertTrue(cartItemsAfter.isEmpty(), "Cart should be empty after clear");
    }

    @Test
    void testClearCart_EmptyCart() {
        boolean result = cartDAO.clearCart(testUserId);
        
        // Clearing an empty cart should still be considered successful
        // because the desired state (empty cart) is achieved
        assertTrue(result, "Should return true even for empty cart");
        
        // Verify cart remains empty
        List<CartItems> cartItems = cartDAO.getCartItems(testUserId);
        assertTrue(cartItems.isEmpty(), "Cart should remain empty");
    }

    @Test
    void testCartItemsProperties() {
        // Add item to cart
        cartDAO.addToCart(testUserId, testProductId1, 2);
        
        List<CartItems> cartItems = cartDAO.getCartItems(testUserId);
        CartItems item = cartItems.get(0);
        
        assertAll("Cart item properties",
            () -> assertTrue(item.getcId() > 0, "Cart item ID should be positive"),
            () -> assertEquals(testUserId, item.getUserId(), "User ID should match"),
            () -> assertEquals(testProductId1, item.getProductId(), "Product ID should match"),
            () -> assertEquals(2, item.getQuantity(), "Quantity should match"),
            () -> assertNotNull(item.getProduct(), "Product should not be null")
        );
    }

    @Test
    void testMultipleUsersCarts() {
        int secondUserId = 2;
        
        // Add test user 2
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO users (name, email, phone, password) VALUES ('User 2', 'user2@example.com', '0987654321', 'password')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Add items to both users' carts
        cartDAO.addToCart(testUserId, testProductId1, 1);
        cartDAO.addToCart(secondUserId, testProductId2, 2);
        
        // Verify each user only sees their own cart items
        List<CartItems> user1Cart = cartDAO.getCartItems(testUserId);
        List<CartItems> user2Cart = cartDAO.getCartItems(secondUserId);
        
        assertEquals(1, user1Cart.size(), "User 1 should have 1 cart item");
        assertEquals(testProductId1, user1Cart.get(0).getProductId());
        
        assertEquals(1, user2Cart.size(), "User 2 should have 1 cart item");
        assertEquals(testProductId2, user2Cart.get(0).getProductId());
    }
}