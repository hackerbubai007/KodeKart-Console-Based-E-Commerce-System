package com.kodewala.kodekart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kodewala.kodekart.bean.User;
import com.kodewala.kodekart.util.DatabaseConnection;

public class UserDAO {

	// Register USER

	public boolean registerUser(User user) {

//    Checking the user already exists or not , if yes then don't insert, if no then insert
//   1st Approach

//		String query1 = "INSERT INTO users(name, email, phone, password) VALUES (?, ?, ?, ?)";
//
//		// Here I use try-with-resources to auto close connection after try block
//		// execute
//
//		try (Connection conn = DatabaseConnection.getConnection();
//				PreparedStatement stmt = conn.prepareStatement(query1)) {
//
//			stmt.setString(1, user.getuName());
//			stmt.setString(2, user.getuEmail());
//			stmt.setString(3, user.getuPhone());
//			stmt.setString(4, user.getuPassword());
//
//			int rowsEffected = stmt.executeUpdate();
//
//			return rowsEffected > 0;
//
//		} catch (SQLException e) {
//			if (e.getErrorCode() == 1062) {
//				System.out.println("user already existes!");
//			} else {
//				e.printStackTrace();
//			}
//			return false;
//
//		}

//    2nd approach

		try (Connection conn = DatabaseConnection.getConnection()) {

			// Step 1: Check if user already exists
			String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
			try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
				checkStmt.setString(1, user.getuEmail());
				try (ResultSet rs = checkStmt.executeQuery()) {
					if (rs.next() && rs.getInt(1) > 0) {
						System.out.println("User already exists!");
						return false; // stop here, don’t insert
					}
				}
			}

			// Step 2: Insert new user
			String insertQuery = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
				stmt.setString(1, user.getuName());
				stmt.setString(2, user.getuEmail());
				stmt.setString(3, user.getuPhone());
				stmt.setString(4, user.getuPassword());

				int rowsAffected = stmt.executeUpdate();
				return rowsAffected > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	// Login USER

	public User loginUser(String email, String password) {

		String query2 = "SELECT * FROM users WHERE email = ? AND password = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query2)) {
			stmt.setString(1, email);
			stmt.setString(2, password);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				User user = new User();
				user.setuId(rs.getInt("id"));
				user.setuName(rs.getString("name"));
				user.setuEmail(rs.getString("email"));
				user.setuPhone(rs.getString("phone"));
				user.setuPassword(rs.getString("password"));
				user.setAdmin(rs.getBoolean("is_admin"));

				return user;

			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return null;

	}

	public boolean emailExists(String email) {

		String query3 = "SELECT * FROM users WHERE email = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query3)) {
			
			stmt.setString(1, email);
			
			ResultSet rs=stmt.executeQuery();
			return rs.next();

		}catch(SQLException e) {
			e.printStackTrace();
			
		}
		return false;
	}
	
	
	
	
	

}
