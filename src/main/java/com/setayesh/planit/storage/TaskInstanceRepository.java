package com.setayesh.planit.storage;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores completed occurrences of recurring tasks.
 * Each row = one (task_id, completed_date).
 */
@Repository
public class TaskInstanceRepository {

    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private final String url;

    public TaskInstanceRepository() {
        this.url = resolveUrl(null);
        initSchema();
    }

    public TaskInstanceRepository(String customDbPath) {
        this.url = resolveUrl(customDbPath);
        initSchema();
    }

    private String resolveUrl(String customPath) {
        if (System.getenv("GITHUB_ACTIONS") != null) {
            return "jdbc:h2:mem:planit;DB_CLOSE_DELAY=-1";
        } else if (customPath != null) {
            return "jdbc:h2:file:" + customPath + ";AUTO_SERVER=TRUE";
        } else {
            String dbPath = System.getProperty("user.dir") + "/planit_db";
            return "jdbc:h2:file:" + dbPath + ";AUTO_SERVER=TRUE";
        }
    }

    private void initSchema() {
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS task_instances_completed (
                        id IDENTITY PRIMARY KEY,
                        task_id UUID NOT NULL,
                        completed_date DATE NOT NULL
                    );
                    """);
        } catch (SQLException e) {
            System.err.println("⚠️ Error initializing task_instances_completed schema: " + e.getMessage());
        }
    }

    /**
     * Mark a specific occurrence as completed for a given date.
     */
    public void markCompleted(UUID taskId, LocalDate date) {
        String sql = "INSERT INTO task_instances_completed(task_id, completed_date) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, taskId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("⚠️ Error saving completed instance: " + e.getMessage());
        }
    }

    /**
     * Check if the task occurrence for 'date' is already completed.
     */
    public boolean isCompletedOnDate(UUID taskId, LocalDate date) {
        String sql = "SELECT 1 FROM task_instances_completed WHERE task_id = ? AND completed_date = ?";
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, taskId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error checking task instance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete all occurrences for a task (e.g., when deleting the task).
     */
    public void deleteForTask(UUID taskId) {
        String sql = "DELETE FROM task_instances_completed WHERE task_id = ?";
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠️ Error deleting task instances: " + e.getMessage());
        }
    }

    public boolean exists(UUID taskId, LocalDate date) {
        String sql = "SELECT 1 FROM task_instances_completed WHERE task_id = ? AND completed_date = ?";
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, taskId);
            ps.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("⚠️ Error checking instance: " + e.getMessage());
            return false;
        }
    }

    public List<LocalDate> findCompletedDates(UUID taskId) {
        List<LocalDate> list = new ArrayList<>();
        String sql = "SELECT completed_date FROM task_instances_completed WHERE task_id = ?";

        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, taskId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getDate("completed_date").toLocalDate());
                }
            }

        } catch (SQLException e) {
            System.err.println("⚠️ Error loading instance dates: " + e.getMessage());
        }

        return list;
    }

}