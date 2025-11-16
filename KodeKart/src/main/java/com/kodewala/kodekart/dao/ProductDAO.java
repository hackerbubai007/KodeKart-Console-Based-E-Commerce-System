package com.kodewala.kodekart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kodewala.kodekart.bean.Product;
import com.kodewala.kodekart.util.DatabaseConnection;

public class ProductDAO {

	// CURD OPERATION
	// C - Create......

	public boolean addProduct(Product product) {

		String query = "INSERT INTO products (name, category, price, quantity, description) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, product.getpName());
			stmt.setString(2, product.getpCategory());
			stmt.setDouble(3, product.getpPrice());
			stmt.setInt(4, product.getpQuantity());
			stmt.setString(5, product.getpDescription());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;

	}

	// U - Update

	public boolean updateProduct(Product product) {

		String query = "UPDATE products SET name = ?, category = ?, price = ?,   quantity = ?, description = ? WHERE id = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, product.getpName());
			stmt.setString(2, product.getpCategory());
			stmt.setDouble(3, product.getpPrice());
			stmt.setInt(4, product.getpQuantity());
			stmt.setString(5, product.getpDescription());
			stmt.setInt(6, product.getpId());

			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	// D - Delete........

	public boolean deleteProduct(int productId) {

		String query = "DELETE FROM products WHERE id=?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, productId);

			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	// R - Read......

	public List<Product> getAllProducts() {
		List<Product> products = new ArrayList<>();
		String query = "SELECT * FROM products WHERE quantity > 0";

		try (Connection conn = DatabaseConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Product product = new Product();
				product.setpId(rs.getInt("id"));
				product.setpName(rs.getString("name"));
				product.setpCategory(rs.getString("category"));
				product.setpPrice(rs.getDouble("price"));
				product.setpQuantity(rs.getInt("quantity"));
				product.setpDescription(rs.getString("description"));

				products.add(product);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
	}

	// Search by Keyword(such part of a string available in string words)......

	public List<Product> searchProducts(String keyword) {

		List<Product> products = new ArrayList<>();

		String query = "SELECT * FROM products WHERE (name LIKE ? OR category LIKE ? OR description LIKE ? ) AND quantity >0 ";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			String searchPattern = "%" + keyword + "%";

			stmt.setString(1, searchPattern);
			stmt.setString(2, searchPattern);
			stmt.setString(3, searchPattern);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				Product product = new Product();

				product.setpId(rs.getInt("id"));
				product.setpName(rs.getString("name"));
				product.setpCategory(rs.getString("category"));
				product.setpPrice(rs.getDouble("price"));
				product.setpQuantity(rs.getInt("quantity"));
				product.setpDescription(rs.getString("description"));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;

	}

	// Search Product by ID....

	public Product getProductById(int productId) {

		String query = "SELECT * FROM products WHERE id =?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, productId);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {

				Product product = new Product();

				product.setpId(rs.getInt("id"));

				product.setpName(rs.getString("name"));
				product.setpCategory(rs.getString("category"));
				product.setpPrice(rs.getDouble("price"));
				product.setpQuantity(rs.getInt("quantity"));
				product.setpDescription(rs.getString("description"));

				return product;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	// Update Product Quantity......

	public boolean updateProductByQuantity(int productId, int newQuantity) {

		String query = "UPDATE products SET quantity=? WHERE id=?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, newQuantity);
			stmt.setInt(2, productId);

			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;

	}

}
