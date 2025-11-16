package com.kodewala.kodekart.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IKodeKartServiceTest {

    @Test
    void testInterfaceMethodsExist() {
        // Test that all required methods are declared in the interface
      
        
        // Check user management methods
        assertDoesNotThrow(() -> {
            IKodeKartService.class.getMethod("registerUser");
            IKodeKartService.class.getMethod("loginUser");
            IKodeKartService.class.getMethod("logoutUser");
        });
        
        // Check product management methods
        assertDoesNotThrow(() -> {
            IKodeKartService.class.getMethod("viewProducts");
            IKodeKartService.class.getMethod("searchProduct");
            IKodeKartService.class.getMethod("viewAllProducts");
            IKodeKartService.class.getMethod("addProduct");
            IKodeKartService.class.getMethod("updateProduct");
            IKodeKartService.class.getMethod("deleteProduct");
        });
        
        // Check cart management methods
        assertDoesNotThrow(() -> {
            IKodeKartService.class.getMethod("addToCart");
            IKodeKartService.class.getMethod("viewCart");
            IKodeKartService.class.getMethod("removeFromCart");
            IKodeKartService.class.getMethod("placeOrder");
        });
        
        // Check order management methods
        assertDoesNotThrow(() -> {
            IKodeKartService.class.getMethod("viewOrderHistory");
            IKodeKartService.class.getMethod("viewAllOrders");
        });
        
        // Check menu navigation methods
        assertDoesNotThrow(() -> {
            IKodeKartService.class.getMethod("showHomeMenu");
            IKodeKartService.class.getMethod("showUserMenu");
            IKodeKartService.class.getMethod("showAdminMenu");
        });
        
        // Check utility methods
        assertDoesNotThrow(() -> {
            IKodeKartService.class.getMethod("getIntInput");
            IKodeKartService.class.getMethod("getDoubleInput");
            IKodeKartService.class.getMethod("getCurrentUser");
            IKodeKartService.class.getMethod("isUserLoggedIn");
            IKodeKartService.class.getMethod("isAdminUser");
        });
    }

    @Test
    void testInterfaceIsPublic() {
        assertTrue(IKodeKartService.class.isInterface(), "Should be an interface");
        assertTrue(java.lang.reflect.Modifier.isPublic(IKodeKartService.class.getModifiers()), 
                  "Interface should be public");
    }


}