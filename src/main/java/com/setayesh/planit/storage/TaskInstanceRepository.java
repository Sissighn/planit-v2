package com.setayesh.planit.storage;

import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.sql.Date;

@Repository
public class TaskInstanceRepository {

    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private final String url;

    public TaskInstanceRepository() {
        this.url = resolveUrl(null);
    }

    public void markCompleted(UUID taskId, LocalDate date) {
        String sql = "INSERT INTO task_instances_completed (task_id, completed_date) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, taskId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("⚠ Error marking completed: " + e.getMessage());
        }
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

    public boolean isCompletedOn(UUID taskId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM task_instances_completed WHERE task_id=? AND completed_date=?";

        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, taskId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("⚠ Error checking completion: " + e.getMessage());
            return false;
        }
    }
}
