package com.kodewala.kodekart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.kodewala.kodekart.bean.CartItems;
import com.kodewala.kodekart.bean.Order;
import com.kodewala.kodekart.bean.OrderItem;
import com.kodewala.kodekart.bean.Product;
import com.kodewala.kodekart.bean.User;
import com.kodewala.kodekart.dao.CartDAO;
import com.kodewala.kodekart.dao.OrderDAO;
import com.kodewala.kodekart.dao.ProductDAO;
import com.kodewala.kodekart.dao.UserDAO;

public class KodeKartServiceImpl implements IKodeKartService {
    
    private Scanner sc;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private User currentUser;

    public KodeKartServiceImpl() {
        sc = new Scanner(System.in);
        userDAO = new UserDAO();
        productDAO = new ProductDAO();
        cartDAO = new CartDAO();
        orderDAO = new OrderDAO();
        currentUser = null;
    }

    @Override
    public int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a number: ");
            }
        }
    }

    @Override
    public double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a number: ");
            }
        }
    }

    @Override
    public void showHomeMenu() {
        System.out.println("\n--- Home Menu ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                loginUser();
                break;
            case 3:
                System.out.println("Thank you for using KodeKart!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option! Please try again.");
        }
    }

    @Override
    public void showUserMenu() {
        System.out.println("\n--- User Menu ---");
        System.out.println("1. View Products");
        System.out.println("2. Search Product");
        System.out.println("3. Add to Cart");
        System.out.println("4. View Cart");
        System.out.println("5. Place Order");
        System.out.println("6. View Order History");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                viewProducts();
                break;
            case 2:
                searchProduct();
                break;
            case 3:
                addToCart();
                break;
            case 4:
                viewCart();
                break;
            case 5:
                placeOrder();
                break;
            case 6:
                viewOrderHistory();
                break;
            case 7:
                logoutUser();
                break;
            default:
                System.out.println("Invalid option! Please try again.");
        }
    }

    @Override
    public void showAdminMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1. Add Product");
        System.out.println("2. Update Product");
        System.out.println("3. Delete Product");
        System.out.println("4. View All Products");
        System.out.println("5. View All Orders");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                addProduct();
                break;
            case 2:
                updateProduct();
                break;
            case 3:
                deleteProduct();
                break;
            case 4:
                viewAllProducts();
                break;
            case 5:
                viewAllOrders();
                break;
            case 6:
                logoutUser();
                break;
            default:
                System.out.println("Invalid option! Please try again.");
        }
    }

    @Override
    public void registerUser() {
        System.out.println("\n--- User Registration ---");
        System.out.print("Enter name: ");
        String name = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        if (userDAO.emailExists(email)) {
            System.out.println("Email already exists! Please use a different email.");
            return;
        }

        System.out.print("Enter phone: ");
        String phone = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User user = new User(name, email, phone, password);

        if (userDAO.registerUser(user)) {
            System.out.println("Registration Successfull! You can now login.");
        } else {
            System.out.println("Registration failed! Please try again.");
        }
    }

    @Override
    public void loginUser() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter email: ");
        String email = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User user = userDAO.loginUser(email, password);

        if (user != null) {
            currentUser = user;
            if (user.isAdmin()) {
                System.out.println("Admin login successful! Welcome, " + user.getuName());
            } else {
                System.out.println("Login successful! Welcome, " + user.getuName());
            }
        } else {
            System.out.println("Invalid email or password! Please try again.");
        }
    }

    @Override
    public void logoutUser() {
        currentUser = null;
        System.out.println("Logout Successfully!");
    }

    @Override
    public void viewProducts() {
        System.out.println("\n--- Available Products ---");
        List<Product> products = productDAO.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.printf("%-5s %-20s %-15s %-10s %-8s %-30s\n", 
                         "ID", "Name", "Category", "Price", "Qty", "Description");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (Product product : products) {
            System.out.printf("%-5d %-20s %-15s $%-9.2f %-8d %-30s\n", 
                            product.getpId(), product.getpName(),
                            product.getpCategory(), product.getpPrice(), 
                            product.getpQuantity(), product.getpDescription());
        }
    }

    @Override
    public void searchProduct() {
        System.out.print("\nEnter search keyword: ");
        String keyword = sc.nextLine();

        List<Product> products = productDAO.searchProducts(keyword);

        if (products.isEmpty()) {
            System.out.println("No products found matching your search.");
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        System.out.printf("%-5s %-20s %-15s %-10s %-8s %-30s\n", 
                         "ID", "Name", "Category", "Price", "Qty", "Description");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (Product product : products) {
            System.out.printf("%-5d %-20s %-15s $%-9.2f %-8d %-30s\n",
                            product.getpId(), product.getpName(), 
                            product.getpCategory(), product.getpPrice(), 
                            product.getpQuantity(), product.getpDescription());
        }
    }

    @Override
    public void addToCart() {
        viewProducts();
        System.out.println("\nEnter product ID to add to cart: ");
        int productId = getIntInput();

        System.out.print("Enter Quantity: ");
        int quantity = getIntInput();

        Product product = productDAO.getProductById(productId);

        if (product == null) {
            System.out.println("Invalid product ID!");
            return;
        }
        if (product.getpQuantity() < quantity) {
            System.out.println("Insufficient stock! Available quantity: " + product.getpQuantity());
            return;
        }

        if (cartDAO.addToCart(currentUser.getuId(), productId, quantity)) {
            System.out.println("Product added to cart successfully!");
        } else {
            System.out.println("Failed to add product to cart!");
        }
    }

    @Override
    public void viewCart() {
        System.out.println("\n--- Your Cart ---");
        List<CartItems> cartItems = cartDAO.getCartItems(currentUser.getuId());

        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        double total = 0;
        System.out.printf("%-5s %-20s %-10s %-8s %-10s\n", "ID", "Product", "Price", "Qty", "Subtotal");
        System.out.println("-----------------------------------------------------");

        for (CartItems item : cartItems) {
            double subTotal = item.getProduct().getpPrice() * item.getQuantity();
            total += subTotal;
            System.out.printf("%-5d %-20s $%-9.2f %-8d $%-9.2f\n", 
                            item.getcId(), item.getProduct().getpName(),
                            item.getProduct().getpPrice(), item.getQuantity(), subTotal);
        }

        System.out.println("-----------------------------------------------------");
        System.out.printf("Total: $%.2f\n", total);

        System.out.println("\n1. Remove item from cart");
        System.out.println("2. Back to menu");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        if (choice == 1) {
            removeFromCart();
        }
    }

    @Override
    public void removeFromCart() {
        System.out.print("Enter cart item ID to remove: ");
        int cartItemId = getIntInput();
        if (cartDAO.removeFromCart(cartItemId)) {
            System.out.println("Item removed from cart successfully!");
        } else {
            System.out.println("Failed to remove item from cart!");
        }
    }

    @Override
    public void placeOrder() {
        List<CartItems> cartItems = cartDAO.getCartItems(currentUser.getuId());

        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty! Add some products first.");
            return;
        }

        double total = 0;
        Order order = new Order();
        order.setUserId(currentUser.getuId());

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItems cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setOrderItemQuentity(cartItem.getQuantity());
            orderItem.setOrderItemPrice(cartItem.getProduct().getpPrice());
            orderItems.add(orderItem);
            total += cartItem.getProduct().getpPrice() * cartItem.getQuantity();
        }

        order.setTotalAmount(total);
        order.setItems(orderItems);

        System.out.printf("\nOrder Total: $%.2f\n", total);
        System.out.print("Confirm order? (yes/no): ");
        String confirm = sc.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {
            int orderId = orderDAO.createOrder(order);
            if (orderId != -1) {
                cartDAO.clearCart(currentUser.getuId());
                System.out.println("Order placed successfully! Order ID: " + orderId);
            } else {
                System.out.println("Failed to place order! Please try again.");
            }
        } else {
            System.out.println("Order cancelled.");
        }
    }

    @Override
    public void viewOrderHistory() {
        System.out.println("\n--- Your Order History ---");
        List<Order> orders = orderDAO.getUserOrders(currentUser.getuId());

        if (orders.isEmpty()) {
            System.out.println("No order found");
            return;
        }

        for (Order order : orders) {
            System.out.println("\nOrder ID: " + order.getoId());
            System.out.println("Date: " + order.getOrderDate());
            System.out.println("Total: $" + order.getTotalAmount());
            System.out.println("Items:");

            for (OrderItem item : order.getItems()) {
                System.out.printf("  - %s (Qty: %d, Price: $%.2f)\n", 
                                item.getProduct().getpName(),
                                item.getOrderItemQuentity(), item.getOrderItemPrice());
            }
            System.out.println("----------------------------");
        }
    }

    @Override
    public void viewAllProducts() {
        System.out.println("\n--- All Products ---");
        List<Product> products = productDAO.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.printf("%-5s %-20s %-15s %-10s %-8s %-30s\n", 
                         "ID", "Name", "Category", "Price", "Qty", "Description");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (Product product : products) {
            System.out.printf("%-5d %-20s %-15s $%-9.2f %-8d %-30s\n", 
                            product.getpId(), product.getpName(),
                            product.getpCategory(), product.getpPrice(), 
                            product.getpQuantity(), product.getpDescription());
        }
    }

    @Override
    public void addProduct() {
        System.out.println("\n--- Add New Product ---");
        System.out.print("Enter product name: ");
        String name = sc.nextLine();

        System.out.print("Enter category: ");
        String category = sc.nextLine();

        System.out.print("Enter price: ");
        double price = getDoubleInput();

        System.out.print("Enter quantity: ");
        int quantity = getIntInput();

        System.out.print("Enter description: ");
        String description = sc.nextLine();

        Product product = new Product(name, category, price, quantity, description);

        if (productDAO.addProduct(product)) {
            System.out.println("Product added successfully!");
        } else {
            System.out.println("Failed to add product!");
        }
    }

    @Override
    public void updateProduct() {
        viewAllProducts();
        System.out.println("\nEnter product ID to update: ");
        int productId = getIntInput();

        Product product = productDAO.getProductById(productId);

        if (product == null) {
            System.out.println("Invalid product ID!");
            return;
        }

        System.out.println("Current details:");
        System.out.println("Name: " + product.getpName());
        System.out.println("Category: " + product.getpCategory());
        System.out.println("Price: " + product.getpPrice());
        System.out.println("Quantity: " + product.getpQuantity());
        System.out.println("Description: " + product.getpDescription());

        System.out.println("\nEnter new details (leave blank to keep current):");
        System.out.print("Name: ");
        String name = sc.nextLine();
        if (!name.isEmpty()) product.setpName(name);

        System.out.print("Category: ");
        String category = sc.nextLine();
        if (!category.isEmpty()) product.setpCategory(category);

        System.out.print("Price: ");
        String priceStr = sc.nextLine();
        if (!priceStr.isEmpty()) product.setpPrice(Double.parseDouble(priceStr));

        System.out.print("Quantity: ");
        String quantityStr = sc.nextLine();
        if (!quantityStr.isEmpty()) product.setpQuantity(Integer.parseInt(quantityStr));

        System.out.print("Description: ");
        String description = sc.nextLine();
        if (!description.isEmpty()) product.setpDescription(description);

        if (productDAO.updateProduct(product)) {
            System.out.println("Product updated successfully!");
        } else {
            System.out.println("Failed to update product!");
        }
    }

    @Override
    public void deleteProduct() {
        viewAllProducts();
        System.out.print("\nEnter product ID to delete: ");
        int productId = getIntInput();
        System.out.print("Are you sure? (yes/no): ");
        String confirm = sc.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {
            if (productDAO.deleteProduct(productId)) {
                System.out.println("Product deleted successfully!");
            } else {
                System.out.println("Failed to delete product!");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    @Override
    public void viewAllOrders() {
        System.out.println("\n--- All Orders ---");
        List<Order> orders = orderDAO.getAllOrders();

        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        for (Order order : orders) {
            System.out.println("\nOrder ID: " + order.getoId());
            System.out.println("User ID: " + order.getUserId());
            System.out.println("Date: " + order.getOrderDate());
            System.out.println("Total: $" + order.getTotalAmount());
            System.out.println("Items:");

            for (OrderItem item : order.getItems()) {
                System.out.printf("  - %s (Qty: %d, Price: $%.2f)\n", 
                                item.getProduct().getpName(),
                                item.getOrderItemQuentity(), item.getOrderItemPrice());
            }
            System.out.println("----------------------------");
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    @Override
    public boolean isAdminUser() {
        return currentUser != null && currentUser.isAdmin();
    }
}