-- Create database
CREATE DATABASE kodekart;
USE kodekart;

-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    password VARCHAR(100) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

-- Products table
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    description TEXT
);

-- Cart table
CREATE TABLE cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    product_id INT,
    quantity INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Orders table
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order items table
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert sample admin user
INSERT INTO users (name, email, phone, password, is_admin) 
VALUES ('Admin', 'admin@kodekart.com', '1234567890', 'admin123', TRUE);

-- Insert sample products
INSERT INTO products (name, category, price, quantity, description) VALUES
('Laptop', 'Electronics', 999.99, 10, 'High-performance laptop'),
('Smartphone', 'Electronics', 699.99, 15, 'Latest smartphone model'),
('T-Shirt', 'Clothing', 29.99, 50, 'Cotton t-shirt'),
('Coffee Mug', 'Home', 12.99, 30, 'Ceramic coffee mug'),
('Book', 'Education', 24.99, 25, 'Programming book');