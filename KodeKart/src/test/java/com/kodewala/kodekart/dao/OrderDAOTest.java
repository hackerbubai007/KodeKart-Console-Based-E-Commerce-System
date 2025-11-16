package com.kodewala.kodekart.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kodewala.kodekart.bean.Order;
import com.kodewala.kodekart.bean.OrderItem;
import com.kodewala.kodekart.bean.Product;
import com.kodewala.kodekart.util.DatabaseConnection;

class OrderDAOTest {
    
    private static Connection connection;
    private OrderDAO orderDAO;
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
            // Drop and recreate tables to ensure clean state
            stmt.execute("DROP TABLE IF EXISTS order_items");
            stmt.execute("DROP TABLE IF EXISTS orders");
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
            
            // Create orders table
            stmt.execute("""
                CREATE TABLE orders (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT,
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    total_amount DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
            
            // Create order_items table
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
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO();
        
        // Clean tables and reset data before each test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM orders");
            stmt.execute("DELETE FROM products");
            stmt.execute("DELETE FROM users");
            
            // Reset auto-increment
            stmt.execute("ALTER TABLE order_items AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE orders AUTO_INCREMENT = 1");
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
        List<Product> products = productDAO.getAllProducts();
        if (products.size() >= 2) {
            testProductId1 = products.get(0).getpId();
            testProductId2 = products.get(1).getpId();
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM orders");
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed.");
        }
        // Switch back to main database
        DatabaseConnection.useMainDatabase();
    }

    @Test
    void testCreateOrder_Success() {
        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        
        OrderItem item1 = new OrderItem();
        item1.setProductId(testProductId1);
        item1.setOrderItemQuentity(2);
        item1.setOrderItemPrice(999.99);
        orderItems.add(item1);
        
        OrderItem item2 = new OrderItem();
        item2.setProductId(testProductId2);
        item2.setOrderItemQuentity(1);
        item2.setOrderItemPrice(499.99);
        orderItems.add(item2);
        
        // Create order
        Order order = new Order();
        order.setUserId(testUserId);
        order.setTotalAmount((2 * 999.99) + (1 * 499.99));
        order.setItems(orderItems);
        
        int orderId = orderDAO.createOrder(order);
        
        assertTrue(orderId > 0, "Order should be created successfully with valid ID");
        
        // Verify product quantities were updated
        Product product1 = productDAO.getProductById(testProductId1);
        Product product2 = productDAO.getProductById(testProductId2);
        
        assertEquals(8, product1.getpQuantity(), "Laptop quantity should be reduced by 2");
        assertEquals(14, product2.getpQuantity(), "Phone quantity should be reduced by 1");
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        // Create order items with quantity more than available
        List<OrderItem> orderItems = new ArrayList<>();
        
        OrderItem item = new OrderItem();
        item.setProductId(testProductId1);
        item.setOrderItemQuentity(20); // More than available stock (10)
        item.setOrderItemPrice(999.99);
        orderItems.add(item);
        
        Order order = new Order();
        order.setUserId(testUserId);
        order.setTotalAmount(20 * 999.99);
        order.setItems(orderItems);
        
        int orderId = orderDAO.createOrder(order);
        
        // Should still create order but not reduce quantity below zero
        assertTrue(orderId > 0, "Order should be created even with insufficient stock");
        
        // Verify product quantity was not reduced (due to WHERE quantity >= ? condition)
        Product product = productDAO.getProductById(testProductId1);
        assertEquals(10, product.getpQuantity(), "Product quantity should remain unchanged when insufficient stock");
    }

    @Test
    void testGetUserOrders_Success() {
        // First create an order
        createTestOrder();
        
        List<Order> userOrders = orderDAO.getUserOrders(testUserId);
        
        assertNotNull(userOrders, "User orders list should not be null");
        assertFalse(userOrders.isEmpty(), "User should have orders");
        
        Order order = userOrders.get(0);
        assertEquals(testUserId, order.getUserId());
        assertTrue(order.getTotalAmount() > 0);
        assertNotNull(order.getOrderDate());
        assertNotNull(order.getItems());
        assertFalse(order.getItems().isEmpty());
    }

    @Test
    void testGetUserOrders_NoOrders() {
        List<Order> userOrders = orderDAO.getUserOrders(999); // Non-existent user
        
        assertNotNull(userOrders, "User orders list should not be null");
        assertTrue(userOrders.isEmpty(), "Non-existent user should have no orders");
    }

    @Test
    void testGetAllOrders_Success() {
        // Create multiple orders
        createTestOrder();
        createTestOrder(); // Create second order
        
        List<Order> allOrders = orderDAO.getAllOrders();
        
        assertNotNull(allOrders, "All orders list should not be null");
        assertEquals(2, allOrders.size(), "Should return all orders");
        
        // Verify orders are sorted by date descending (most recent first)
        Order firstOrder = allOrders.get(0);
        Order secondOrder = allOrders.get(1);
        assertTrue(firstOrder.getOrderDate().getTime() >= secondOrder.getOrderDate().getTime());
    }

    @Test
    void testGetAllOrders_NoOrders() {
        List<Order> allOrders = orderDAO.getAllOrders();
        
        assertNotNull(allOrders, "All orders list should not be null");
        assertTrue(allOrders.isEmpty(), "Should return empty list when no orders exist");
    }

    @Test
    void testOrderItems_ContainProductDetails() {
        createTestOrder();
        
        List<Order> userOrders = orderDAO.getUserOrders(testUserId);
        Order order = userOrders.get(0);
        List<OrderItem> items = order.getItems();
        
        assertFalse(items.isEmpty(), "Order should have items");
        
        OrderItem firstItem = items.get(0);
        assertNotNull(firstItem.getProduct(), "Order item should have product details");
        assertNotNull(firstItem.getProduct().getpName(), "Product should have name");
        assertTrue(firstItem.getOrderItemPrice() > 0, "Order item should have price");
        assertTrue(firstItem.getOrderItemQuentity() > 0, "Order item should have quantity");
    }

    @Test
    void testOrderProperties() {
        createTestOrder();
        
        List<Order> userOrders = orderDAO.getUserOrders(testUserId);
        Order order = userOrders.get(0);
        
        assertAll("Order properties",
            () -> assertTrue(order.getoId() > 0, "Order ID should be positive"),
            () -> assertEquals(testUserId, order.getUserId(), "User ID should match"),
            () -> assertTrue(order.getTotalAmount() > 0, "Total amount should be positive"),
            () -> assertNotNull(order.getOrderDate(), "Order date should not be null"),
            () -> assertNotNull(order.getItems(), "Order items should not be null")
        );
    }

    @Test
    void testOrderItemProperties() {
        createTestOrder();
        
        List<Order> userOrders = orderDAO.getUserOrders(testUserId);
        Order order = userOrders.get(0);
        OrderItem item = order.getItems().get(0);
        
        assertAll("Order item properties",
            () -> assertTrue(item.getOrderItemId() > 0, "Order item ID should be positive"),
            () -> assertEquals(order.getoId(), item.getOrderId(), "Order ID should match"),
            () -> assertTrue(item.getProductId() > 0, "Product ID should be positive"),
            () -> assertTrue(item.getOrderItemQuentity() > 0, "Quantity should be positive"),
            () -> assertTrue(item.getOrderItemPrice() > 0, "Price should be positive"),
            () -> assertNotNull(item.getProduct(), "Product should not be null")
        );
    }

    // Helper method to create a test order
    private void createTestOrder() {
        List<OrderItem> orderItems = new ArrayList<>();
        
        OrderItem item1 = new OrderItem();
        item1.setProductId(testProductId1);
        item1.setOrderItemQuentity(1);
        item1.setOrderItemPrice(999.99);
        orderItems.add(item1);
        
        Order order = new Order();
        order.setUserId(testUserId);
        order.setTotalAmount(999.99);
        order.setItems(orderItems);
        
        orderDAO.createOrder(order);
    }
}