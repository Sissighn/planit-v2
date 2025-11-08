package com.setayesh.planit.storage;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.Priority;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseTaskRepository implements TaskRepository {

    private static final String DB_PATH = System.getProperty("user.dir") + "/planit_db"; // <-- absolute Pfadangabe
    private static final String URL = "jdbc:h2:file:" + DB_PATH + ";AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public DatabaseTaskRepository() {
        System.out.println("🗂 DatabaseTaskRepository initialized");
        System.out.println("📁 H2 URL = " + URL);
        System.out.println("📂 Working directory = " + System.getProperty("user.dir"));
        initDatabase();
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            // Active tasks
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS tasks (
                            id UUID PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            deadline DATE,
                            priority VARCHAR(50),
                            done BOOLEAN,
                            archived BOOLEAN,
                            created_at TIMESTAMP,
                            updated_at TIMESTAMP
                        );
                    """);

            // Archived tasks
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS archive (
                            id UUID PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            deadline DATE,
                            priority VARCHAR(50),
                            done BOOLEAN,
                            archived BOOLEAN,
                            created_at TIMESTAMP,
                            updated_at TIMESTAMP
                        );
                    """);

        } catch (SQLException e) {
            System.err.println("⚠️ Database init error: " + e.getMessage());
        }
    }

    // --- Active Tasks ---

    @Override
    public List<Task> findAll() {
        return readTable("tasks");
    }

    @Override
    public void saveAll(List<Task> tasks) {
        writeTable("tasks", tasks);
    }

    // --- Archive Tasks ---

    @Override
    public List<Task> loadArchive() {
        return readTable("archive");
    }

    @Override
    public void saveArchive(List<Task> archive) {
        writeTable("archive", archive);
    }

    // --- Internal Helpers ---

    private List<Task> readTable(String table) {
        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM " + table;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                String title = rs.getString("title");
                LocalDate deadline = (rs.getDate("deadline") != null) ? rs.getDate("deadline").toLocalDate() : null;
                Priority priority = (rs.getString("priority") != null) ? Priority.valueOf(rs.getString("priority"))
                        : null;
                boolean done = rs.getBoolean("done");
                boolean archived = rs.getBoolean("archived");
                LocalDateTime createdAt = rs.getTimestamp("created_at") != null
                        ? rs.getTimestamp("created_at").toLocalDateTime()
                        : LocalDateTime.now();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at") != null
                        ? rs.getTimestamp("updated_at").toLocalDateTime()
                        : LocalDateTime.now();

                Task t = new Task(id, title, deadline, priority, done, archived, createdAt, updatedAt);
                tasks.add(t);
            }

        } catch (SQLException e) {
            System.err.println("⚠️ Error reading from " + table + ": " + e.getMessage());
        }

        return tasks;
    }

    private void writeTable(String table, List<Task> tasks) {
        String truncate = "TRUNCATE TABLE " + table;
        String insert = """
                    INSERT INTO %s (id, title, deadline, priority, done, archived, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """.formatted(table);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false); // <-- Transaktionsmodus aktivieren

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(truncate);
            }

            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                for (Task t : tasks) {
                    ps.setObject(1, t.getId());
                    ps.setString(2, t.getTitle());
                    ps.setObject(3, t.getDeadline());
                    ps.setString(4, t.getPriority() != null ? t.getPriority().name() : null);
                    ps.setBoolean(5, t.isDone());
                    ps.setBoolean(6, t.isArchived());
                    ps.setTimestamp(7, Timestamp.valueOf(t.getCreatedAt()));
                    ps.setTimestamp(8, Timestamp.valueOf(t.getUpdatedAt()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit(); // <-- Änderungen dauerhaft speichern

        } catch (SQLException e) {
            System.err.println("⚠️ Error saving to " + table + ": " + e.getMessage());
        }
    }

}
