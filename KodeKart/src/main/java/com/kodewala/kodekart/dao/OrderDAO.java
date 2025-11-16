package com.kodewala.kodekart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kodewala.kodekart.bean.Order;
import com.kodewala.kodekart.bean.OrderItem;
import com.kodewala.kodekart.bean.Product;
import com.kodewala.kodekart.util.DatabaseConnection;

public class OrderDAO {

	public int createOrder(Order order) {

		String orderQuery = "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)";
		String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

		Connection conn = null;

		try {

			conn = DatabaseConnection.getConnection();
			conn.setAutoCommit(false);

			// Create Order......
			PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
			orderStmt.setInt(1, order.getUserId());
			orderStmt.setDouble(2, order.getTotalAmount());
			orderStmt.executeUpdate();

			// get JDBC generated order ID ....

			ResultSet rs = orderStmt.getGeneratedKeys();
			int orderId = 0;
			if (rs.next()) {
				orderId = rs.getInt(1);
			}

			// Add order items.....
			PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
			for (OrderItem item : order.getItems()) {
				itemStmt.setInt(1, orderId);
				itemStmt.setInt(2, item.getProductId());
				itemStmt.setInt(3, item.getOrderItemQuentity());
				itemStmt.setDouble(4, item.getOrderItemPrice());

				itemStmt.addBatch();

				// Update product Quantity...

				updateProductQuantity(conn, item.getProductId(), item.getOrderItemQuentity());

			}

			itemStmt.executeBatch();
			conn.commit();
			return orderId;

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			if (conn != null) {

				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return -1;

	}

	private void updateProductQuantity(Connection conn, int productId, int quantity) throws SQLException {
		String query = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setInt(1, quantity);
		stmt.setInt(2, productId);
		stmt.setInt(3, quantity);
		stmt.executeUpdate();
	}

	public List<Order> getUserOrders(int userId) {

		List<Order> orders = new ArrayList<>();
		String query = "SELECT * FROM orders WHERE user_id=? ORDER BY order_date DESC";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, userId);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Order order = new Order();
				order.setoId(rs.getInt("id"));
				order.setUserId(rs.getInt("user_id"));
				order.setOrderDate(rs.getTimestamp("order_date"));
				order.setTotalAmount(rs.getDouble("total_amount"));
				order.setItems(getOrderItems(conn, order.getoId()));
				orders.add(order);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	public List<Order> getAllOrders() {
		List<Order> orders = new ArrayList<>();

		String query = "SELECT * FROM orders ORDER BY order_date DESC";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				Order order = new Order();
				order.setoId(rs.getInt("id"));
				order.setUserId(rs.getInt("user_id"));
				order.setOrderDate(rs.getTimestamp("order_date"));
				order.setTotalAmount(rs.getDouble("total_amount"));
				order.setItems(getOrderItems(conn, order.getoId()));

				orders.add(order);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return orders;

	}

	private List<OrderItem> getOrderItems(Connection conn, int orderId) throws SQLException {

		List<OrderItem> items = new ArrayList<>();
		String query = "SELECT oi.*, p.name FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";

		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setInt(1, orderId);
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {

			OrderItem item = new OrderItem();

			item.setOrderItemId(rs.getInt("id"));
			item.setOrderId(rs.getInt("order_id"));
			item.setProductId(rs.getInt("product_id"));
			item.setOrderItemQuentity(rs.getInt("quantity"));
			item.setOrderItemPrice(rs.getDouble("price"));

			Product product = new Product();

			product.setpId(rs.getInt("product_id"));
			product.setpName(rs.getString("name"));

			item.setProduct(product);

			items.add(item);

		}

		return items;
	}

}
