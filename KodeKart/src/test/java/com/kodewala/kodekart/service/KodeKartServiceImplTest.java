package com.kodewala.kodekart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kodewala.kodekart.bean.Product;
import com.kodewala.kodekart.bean.User;
import com.kodewala.kodekart.dao.CartDAO;
import com.kodewala.kodekart.dao.OrderDAO;
import com.kodewala.kodekart.dao.ProductDAO;
import com.kodewala.kodekart.dao.UserDAO;

class KodeKartServiceImplTest {
    
    private KodeKartServiceImpl service;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        // Mock DAOs
        userDAO = mock(UserDAO.class);
        productDAO = mock(ProductDAO.class);
        cartDAO = mock(CartDAO.class);
        orderDAO = mock(OrderDAO.class);
        
        // Create service instance
        service = new KodeKartServiceImpl();
        
        // Inject mocked DAOs using reflection
        injectMockedDAOs();
        
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    private void injectMockedDAOs() {
        try {
            var userDAOField = KodeKartServiceImpl.class.getDeclaredField("userDAO");
            userDAOField.setAccessible(true);
            userDAOField.set(service, userDAO);
            
            var productDAOField = KodeKartServiceImpl.class.getDeclaredField("productDAO");
            productDAOField.setAccessible(true);
            productDAOField.set(service, productDAO);
            
            var cartDAOField = KodeKartServiceImpl.class.getDeclaredField("cartDAO");
            cartDAOField.setAccessible(true);
            cartDAOField.set(service, cartDAO);
            
            var orderDAOField = KodeKartServiceImpl.class.getDeclaredField("orderDAO");
            orderDAOField.setAccessible(true);
            orderDAOField.set(service, orderDAO);
            
        } catch (Exception e) {
            fail("Failed to inject mocked DAOs: " + e.getMessage());
        }
    }
    
    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }
    
    private void setCurrentUser(User user) {
        try {
            var currentUserField = KodeKartServiceImpl.class.getDeclaredField("currentUser");
            currentUserField.setAccessible(true);
            currentUserField.set(service, user);
        } catch (Exception e) {
            fail("Failed to set current user: " + e.getMessage());
        }
    }

    // Test 1: Constructor and Basic Setup
    @Test
    void testConstructor() {
        assertNotNull(service, "Service should be initialized");
        assertTrue(service instanceof IKodeKartService, "Should implement the interface");
    }



    
   
    @Test
    void testLogoutUser() {
        User user = new User("Test User", "test@example.com", "1234567890", "password");
        setCurrentUser(user);
        
        assertTrue(service.isUserLoggedIn(), "User should be logged in before logout");
        
        service.logoutUser();
        
        assertFalse(service.isUserLoggedIn(), "User should not be logged in after logout");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Logout"), "Should show logout message");
    }

    // Test 4: Product Management
    @Test
    void testViewProducts_WithProducts() {
        Product product = new Product("Laptop", "Electronics", 999.99, 10, "Test laptop");
        product.setpId(1);
        
        when(productDAO.getAllProducts()).thenReturn(Arrays.asList(product));
        
        service.viewProducts();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Products"), "Should show products");
    }

    @Test
    void testViewProducts_Empty() {
        when(productDAO.getAllProducts()).thenReturn(Arrays.asList());
        
        service.viewProducts();
        
        String output = outputStream.toString();
        assertTrue(output.contains("No products") || output.contains("Products"), 
                  "Should handle empty products");
    }

//    @Test
//    void testSearchProduct() {
//        provideInput("Laptop\n");
//        
//        Product product = new Product("Laptop", "Electronics", 999.99, 10, "Test Laptop");
//        when(productDAO.searchProducts("Laptop")).thenReturn(Arrays.asList(product));
//        
//        service.searchProduct();
//        
//        String output = outputStream.toString();
//        assertTrue(output.contains("Search") || output.contains("keyword"), 
//                  "Should show search functionality");
//    }

    @Test
    void testViewAllProducts() {
        Product product = new Product("Laptop", "Electronics", 999.99, 10, "Test laptop");
        when(productDAO.getAllProducts()).thenReturn(Arrays.asList(product));
        
        service.viewAllProducts();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Products"), "Should show all products");
    }

   
    // Test 5: User State Methods
    @Test
    void testGetCurrentUser_NoUser() {
        assertNull(service.getCurrentUser(), "Should return null when no user logged in");
    }

    @Test
    void testGetCurrentUser_WithUser() {
        User user = new User("Test User", "test@example.com", "1234567890", "password");
        setCurrentUser(user);
        
        assertEquals(user, service.getCurrentUser(), "Should return current user");
    }

    @Test
    void testIsUserLoggedIn_False() {
        assertFalse(service.isUserLoggedIn(), "Should return false when no user logged in");
    }

    @Test
    void testIsUserLoggedIn_True() {
        User user = new User("Test User", "test@example.com", "1234567890", "password");
        setCurrentUser(user);
        
        assertTrue(service.isUserLoggedIn(), "Should return true when user logged in");
    }

    @Test
    void testIsAdminUser_False() {
        assertFalse(service.isAdminUser(), "Should return false when no admin user");
    }

    @Test
    void testIsAdminUser_True() {
        User adminUser = new User("Admin", "admin@kodekart.com", "1234567890", "admin123");
        adminUser.setAdmin(true);
        setCurrentUser(adminUser);
        
        assertTrue(service.isAdminUser(), "Should return true when admin user logged in");
    }

    @Test
    void testIsAdminUser_RegularUser() {
        User regularUser = new User("Regular User", "user@example.com", "1234567890", "password");
        regularUser.setAdmin(false);
        setCurrentUser(regularUser);
        
        assertFalse(service.isAdminUser(), "Should return false for regular user");
    }

    // Test 6: Menu Methods (Basic call without full execution)
  
    // Test 7: Cart Operations (with logged-in user)
   

    @Test
    void testViewCart_WithUser() {
        User user = new User("Test User", "test@example.com", "1234567890", "password");
        user.setuId(1);
        setCurrentUser(user);
        
        when(cartDAO.getCartItems(1)).thenReturn(Arrays.asList());
        
        service.viewCart();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Cart"), "Should show cart view");
    }

   

    // Test 8: Order Management
    @Test
    void testViewOrderHistory_WithUser() {
        User user = new User("Test User", "test@example.com", "1234567890", "password");
        user.setuId(1);
        setCurrentUser(user);
        
        when(orderDAO.getUserOrders(1)).thenReturn(Arrays.asList());
        
        service.viewOrderHistory();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Order History"), "Should show order history");
    }

    @Test
    void testViewAllOrders() {
        when(orderDAO.getAllOrders()).thenReturn(Arrays.asList());
        
        service.viewAllOrders();
        
        String output = outputStream.toString();
        assertTrue(output.contains("All Orders"), "Should show all orders");
    }

    @Test
    void testPlaceOrder() {
        User user = new User("Test User", "test@example.com", "1234567890", "password");
        user.setuId(1);
        setCurrentUser(user);
        
        provideInput("no\n"); // Don't confirm order
        
        when(cartDAO.getCartItems(1)).thenReturn(Arrays.asList());
        
        service.placeOrder();
        
        String output = outputStream.toString();
        assertTrue(output.contains("empty") || output.contains("Order"), 
                  "Should handle order placement");
    }
}