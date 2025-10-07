package com.grievance.dao;

import com.grievance.model.Grievance;
import com.grievance.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Grievance-related database operations (CRUD).
 */
public class GrievanceDAO {

    // SQL Statements
    private static final String INSERT_GRIEVANCE = "INSERT INTO grievances (user_id, title, description) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_GRIEVANCES = "SELECT * FROM grievances ORDER BY created_at DESC";
    private static final String UPDATE_STATUS = "UPDATE grievances SET status = ?, resolved_at = ? WHERE id = ?";


    // --- CREATE ---
    public boolean createGrievance(Grievance grievance) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(INSERT_GRIEVANCE);
            ps.setInt(1, grievance.getUserId());
            ps.setString(2, grievance.getTitle());
            ps.setString(3, grievance.getDescription());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error creating grievance: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // --- READ ---
    public List<Grievance> getAllGrievances() {
        List<Grievance> grievances = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(SELECT_ALL_GRIEVANCES);
            rs = ps.executeQuery();

            while (rs.next()) {
                grievances.add(extractGrievanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving all grievances: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return grievances;
    }
    
 // --- READ grievances by user ---
    public List<Grievance> getGrievancesByUserId(int userId) {
        List<Grievance> grievances = new ArrayList<>();
        String sql = "SELECT * FROM grievances WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grievances.add(extractGrievanceFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error retrieving user grievances: " + e.getMessage());
        }

        return grievances;
    }
    
 // Search grievances by title or description (case-insensitive)
    public List<Grievance> searchGrievances(String keyword) {
        List<Grievance> grievances = new ArrayList<>();
        String sql = "SELECT * FROM grievances WHERE title LIKE ? OR description LIKE ? ORDER BY created_at DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                grievances.add(extractGrievanceFromResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Database error during search: " + e.getMessage());
        }
        
        return grievances;
    }
    
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) AS total FROM grievances WHERE status = ?";
        int count = 0;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error counting grievances by status: " + e.getMessage());
        }

        return count;
    }





    // --- UPDATE ---
    /**
     * Updates the status of a specific grievance.
     * @param grievanceId The ID of the grievance to update.
     * @param newStatus The new status (OPEN, IN_PROGRESS, RESOLVED).
     * @return true if update was successful, false otherwise.
     */
    public boolean updateGrievanceStatus(int grievanceId, String newStatus) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(UPDATE_STATUS);
            ps.setString(1, newStatus);

            // Set resolved_at only if the status is RESOLVED
            if ("RESOLVED".equalsIgnoreCase(newStatus)) {
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                 ps.setTimestamp(2, null); // Set to null for OPEN or IN_PROGRESS
            }

            ps.setInt(3, grievanceId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating grievance status: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }


    // --- Utility Methods ---

    private Grievance extractGrievanceFromResultSet(ResultSet rs) throws SQLException {
        Grievance grievance = new Grievance();
        grievance.setId(rs.getInt("id"));
        grievance.setUserId(rs.getInt("user_id"));
        grievance.setTitle(rs.getString("title"));
        grievance.setDescription(rs.getString("description"));
        grievance.setStatus(rs.getString("status"));
        grievance.setCreatedAt(rs.getTimestamp("created_at"));
        grievance.setResolvedAt(rs.getTimestamp("resolved_at"));
        return grievance;
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
