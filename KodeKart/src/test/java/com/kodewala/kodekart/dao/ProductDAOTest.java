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

import com.kodewala.kodekart.bean.Product;
import com.kodewala.kodekart.util.DatabaseConnection;

class ProductDAOTest {
    
    private static Connection connection;
    private ProductDAO productDAO;
    private static int testProductId; // Store the ID of the product we'll test with

    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseConnection.useTestDatabase();
        connection = DatabaseConnection.getConnection();
        assertNotNull(connection, "Database connection should not be null");

        try (Statement stmt = connection.createStatement()) {
            // Drop and recreate to ensure clean state
            stmt.execute("DROP TABLE IF EXISTS products");
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
        }
        System.out.println("Test database setup completed.");
    }

    @BeforeEach
    void init() {
        productDAO = new ProductDAO();
        // Clean the table and insert test data before each test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM products");
            // Reset auto-increment to avoid ID issues
            stmt.execute("ALTER TABLE products AUTO_INCREMENT = 1");
            // Insert sample test data
            stmt.execute("""
                INSERT INTO products (name, category, price, quantity, description) VALUES
                ('Laptop', 'Electronics', 999.99, 10, 'High-performance laptop'),
                ('Smartphone', 'Electronics', 699.99, 15, 'Latest smartphone model'),
                ('T-Shirt', 'Clothing', 29.99, 50, 'Cotton t-shirt'),
                ('Coffee Mug', 'Home', 12.99, 0, 'Ceramic coffee mug')
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Get the ID of the first product for testing
        List<Product> products = productDAO.getAllProducts();
        if (!products.isEmpty()) {
            testProductId = products.get(0).getpId();
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM products");
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
    void testAddProduct_Success() {
        Product product = new Product("Wireless Headphones", "Electronics", 199.99, 25, "Noise cancelling headphones");
        boolean result = productDAO.addProduct(product);
        assertTrue(result, "Product should be added successfully");
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        assertNotNull(products, "Product list should not be null");
        assertEquals(3, products.size(), "Should return only products with quantity > 0");
        
        // Verify first product details
        Product firstProduct = products.get(0);
        assertEquals("Laptop", firstProduct.getpName());
        assertEquals("Electronics", firstProduct.getpCategory());
        assertEquals(999.99, firstProduct.getpPrice(), 0.01);
        assertEquals(10, firstProduct.getpQuantity());
    }

    @Test
    void testGetProductById_Success() {
        Product product = productDAO.getProductById(testProductId);
        assertNotNull(product, "Product should not be null");
        assertEquals("Laptop", product.getpName());
        assertEquals("Electronics", product.getpCategory());
    }

    @Test
    void testGetProductById_NotFound() {
        Product product = productDAO.getProductById(999); // Non-existent ID
        assertNull(product, "Should return null for non-existent product ID");
    }

    @Test
    void testUpdateProduct_Success() {
        // First get the product to update
        Product product = productDAO.getProductById(testProductId);
        assertNotNull(product, "Product should exist before update");
        
        // Update product details
        product.setpName("Updated Laptop");
        product.setpPrice(1099.99);
        product.setpQuantity(5);
        product.setpDescription("Updated description");
        
        boolean result = productDAO.updateProduct(product);
        assertTrue(result, "Product should be updated successfully");
        
        // Verify the update
        Product updatedProduct = productDAO.getProductById(testProductId);
        assertNotNull(updatedProduct, "Updated product should exist");
        assertEquals("Updated Laptop", updatedProduct.getpName());
        assertEquals(1099.99, updatedProduct.getpPrice(), 0.01);
        assertEquals(5, updatedProduct.getpQuantity());
        assertEquals("Updated description", updatedProduct.getpDescription());
    }

    @Test
    void testDeleteProduct_Success() {
        // First, add a new product to delete (to avoid affecting other tests)
        Product productToDelete = new Product("Test Product", "Test", 100.0, 10, "Test description");
        productDAO.addProduct(productToDelete);
        
        // Get the ID of the newly added product
        List<Product> products = productDAO.getAllProducts();
        int lastProductId = products.get(products.size() - 1).getpId();
        
        boolean result = productDAO.deleteProduct(lastProductId);
        assertTrue(result, "Product should be deleted successfully");
        
        // Verify deletion
        Product deletedProduct = productDAO.getProductById(lastProductId);
        assertNull(deletedProduct, "Product should not exist after deletion");
    }

    @Test
    void testDeleteProduct_NotFound() {
        boolean result = productDAO.deleteProduct(999); // Non-existent ID
        assertFalse(result, "Deleting non-existent product should return false");
    }

    @Test
    void testSearchProducts_ByName() {
        List<Product> results = productDAO.searchProducts("Laptop");
        assertNotNull(results, "Search results should not be null");
        assertEquals(1, results.size(), "Should find one product with 'Laptop' in name");
        assertEquals("Laptop", results.get(0).getpName());
    }

    @Test
    void testSearchProducts_ByCategory() {
        List<Product> results = productDAO.searchProducts("Electronics");
        assertNotNull(results, "Search results should not be null");
        assertEquals(2, results.size(), "Should find two products in Electronics category");
    }

    @Test
    void testSearchProducts_ByDescription() {
        List<Product> results = productDAO.searchProducts("Cotton");
        assertNotNull(results, "Search results should not be null");
        assertEquals(1, results.size(), "Should find one product with 'Cotton' in description");
        assertEquals("T-Shirt", results.get(0).getpName());
    }

    @Test
    void testSearchProducts_NoResults() {
        List<Product> results = productDAO.searchProducts("NonExistentProduct");
        assertNotNull(results, "Search results should not be null");
        assertTrue(results.isEmpty(), "Should return empty list for non-existent product");
    }

    @Test
    void testUpdateProductQuantity_Success() {
        boolean result = productDAO.updateProductByQuantity(testProductId, 20);
        assertTrue(result, "Quantity should be updated successfully");
        
        // Verify the update
        Product updatedProduct = productDAO.getProductById(testProductId);
        assertNotNull(updatedProduct, "Product should exist after quantity update");
        assertEquals(20, updatedProduct.getpQuantity(), "Quantity should be updated to 20");
    }

    @Test
    void testUpdateProductQuantity_ProductNotFound() {
        boolean result = productDAO.updateProductByQuantity(999, 20); // Non-existent ID
        assertFalse(result, "Updating quantity of non-existent product should return false");
    }

    @Test
    void testGetAllProducts_ExcludesZeroQuantity() {
        List<Product> products = productDAO.getAllProducts();
        // Should only return products with quantity > 0 (excludes Coffee Mug with quantity 0)
        assertEquals(3, products.size(), "Should exclude products with zero quantity");
        
        // Verify no product with zero quantity is returned
        for (Product product : products) {
            assertTrue(product.getpQuantity() > 0, "All returned products should have quantity > 0");
        }
    }

    @Test
    void testSearchProducts_ExcludesZeroQuantity() {
        List<Product> results = productDAO.searchProducts("Coffee");
        assertNotNull(results, "Search results should not be null");
        assertTrue(results.isEmpty(), "Should exclude products with zero quantity from search results");
    }

    @Test
    void testProductProperties() {
        Product product = productDAO.getProductById(testProductId);
        assertNotNull(product, "Product should exist for property testing");
        
        assertAll("Product properties",
            () -> assertNotNull(product.getpName(), "Name should not be null"),
            () -> assertNotNull(product.getpCategory(), "Category should not be null"),
            () -> assertTrue(product.getpPrice() > 0, "Price should be positive"),
            () -> assertTrue(product.getpQuantity() >= 0, "Quantity should be non-negative"),
            () -> assertNotNull(product.getpDescription(), "Description should not be null")
        );
    }
}