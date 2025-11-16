package com.kodewala.kodekart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kodewala.kodekart.bean.CartItems;
import com.kodewala.kodekart.bean.Product;
import com.kodewala.kodekart.util.DatabaseConnection;

public class CartDAO {

	public boolean addToCart(int userId, int productId, int quantity) {

		// Check item already exists or not.....

		String checkQuery = "SELECT * FROM cart WHERE user_id = ? AND product_id = ?";
		String insertQuery = "INSERT INTO cart (user_id, product_id, quantity) VALUES(?, ?, ?)";
		String updateQuery = "UPDATE cart SET quantity=quantity+? WHERE user_id=? AND product_id=?";
		try (Connection conn = DatabaseConnection.getConnection()) {

			// check product exists or not....
			PreparedStatement checkStmt = conn.prepareStatement(checkQuery);

			checkStmt.setInt(1, userId);
			checkStmt.setInt(2, productId);

			ResultSet rs = checkStmt.executeQuery();

			if (rs.next()) {

				// update

				PreparedStatement updateStmt = conn.prepareStatement(updateQuery);

				updateStmt.setInt(1, quantity);
				updateStmt.setInt(2, userId);
				updateStmt.setInt(3, productId);

				return updateStmt.executeUpdate() > 0;

			} else {
				// Insert new item to cart......

				PreparedStatement insertStmt = conn.prepareStatement(insertQuery);

				insertStmt.setInt(1, userId);
				insertStmt.setInt(2, productId);
				insertStmt.setInt(3, quantity);
				return insertStmt.executeUpdate() > 0;

			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return false;

	}

	public List<CartItems> getCartItems(int userId) {
		List<CartItems> cartItems = new ArrayList<>();

		String query = "SELECT c.*, p.name, p.price, p.category, p.description "
				+ "FROM cart c JOIN products p ON c.product_id = p.id " + "WHERE c.user_id = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				CartItems item = new CartItems();

				item.setcId(rs.getInt("id"));
				item.setUserId(rs.getInt("user_id"));
				item.setProductId(rs.getInt("product_id"));
				item.setQuantity(rs.getInt("quantity"));

				Product product = new Product();

				product.setpId(rs.getInt("product_id"));
				product.setpName(rs.getString("name"));
				product.setpPrice(rs.getDouble("price"));
				product.setpCategory(rs.getString("category"));
				product.setpDescription(rs.getString("description"));

				// Assigning a product object to a cart item.
				item.setProduct(product);

				cartItems.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cartItems;

	}

	public boolean removeFromCart(int cartItemId) {
		String query = "DELETE FROM cart WHERE id=?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, cartItemId);
			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean clearCart(int userId) {
	    String query = "DELETE FROM cart WHERE user_id=?";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setInt(1, userId);
	        stmt.executeUpdate();
	        return true; // Always return true for clear operation
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

}
