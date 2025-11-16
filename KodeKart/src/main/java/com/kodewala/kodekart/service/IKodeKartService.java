package com.kodewala.kodekart.service;

import java.util.List;

import com.kodewala.kodekart.bean.CartItems;
import com.kodewala.kodekart.bean.Order;
import com.kodewala.kodekart.bean.Product;
import com.kodewala.kodekart.bean.User;


public interface IKodeKartService {
    
    // User Management
    void registerUser();
    void loginUser();
    void logoutUser();
    
    // Product Management
    void viewProducts();
    void searchProduct();
    void viewAllProducts();
    void addProduct();
    void updateProduct();
    void deleteProduct();
    
    // Cart Management
    void addToCart();
    void viewCart();
    void removeFromCart();
    void placeOrder();
    
    // Order Management
    void viewOrderHistory();
    void viewAllOrders();
    
    // Menu Navigation
    void showHomeMenu();
    void showUserMenu();
    void showAdminMenu();
    
    // Utility Methods
    int getIntInput();
    double getDoubleInput();
    
    // Getters for current state
    User getCurrentUser();
    boolean isUserLoggedIn();
    boolean isAdminUser();
}