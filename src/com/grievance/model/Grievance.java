package com.grievance.model;

import java.sql.Timestamp;

/**
 * Represents a grievance in the system (maps to the 'grievances' table).
 */
public class Grievance {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String status; // ENUM: OPEN, IN_PROGRESS, RESOLVED
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    // Default Constructor
    public Grievance() {}

    // Constructor for creating a new grievance
    public Grievance(int userId, String title, String description) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = "OPEN"; // Default status
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }

    @Override
    public String toString() {
        return String.format(
            "| ID: %-4d | Status: %-12s | Title: %-30s | Raised By User ID: %-4d | Created: %s |",
            id, status, title, userId, createdAt.toString().substring(0, 16)
        );
    }
}
