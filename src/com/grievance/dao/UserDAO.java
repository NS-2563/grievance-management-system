package com.grievance.dao;

import com.grievance.model.User;
import com.grievance.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User-related database operations (Login, Register, Admin Management).
 */
public class UserDAO {

    private static final String SELECT_USER_BY_CREDENTIALS = "SELECT * FROM users WHERE username = ? AND password = ?";
    private static final String INSERT_USER = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users ORDER BY id";
    private static final String UPDATE_USER_ROLE = "UPDATE users SET role = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

    // --- LOGIN ---
    public User login(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(SELECT_USER_BY_CREDENTIALS);
            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();

            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return user;
    }

    // --- REGISTER ---
    public boolean register(String username, String password) {
        return registerWithRole(username, password, "USER"); // Default role
    }

    // --- REGISTER WITH ROLE (ADMIN USES THIS) ---
    public boolean registerWithRole(String username, String password, String role) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(INSERT_USER);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Error: Username '" + username + "' is already taken.");
            } else {
                System.err.println("Database error during registration: " + e.getMessage());
            }
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // --- GET ALL USERS ---
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(SELECT_ALL_USERS);
            rs = ps.executeQuery();

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving all users: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return users;
    }

    // --- UPDATE USER ROLE ---
    public boolean updateUserRole(int userId, String newRole) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(UPDATE_USER_ROLE);
            ps.setString(1, newRole);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating user role: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // --- DELETE USER ---
    public boolean deleteUser(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(DELETE_USER);
            ps.setInt(1, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error deleting user: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // --- HELPER ---
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) DBUtil.closeConnection(conn);
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
