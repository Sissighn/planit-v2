package com.setayesh.planit.storage;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.Priority;
import com.setayesh.planit.core.RepeatFrequency;

import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class DatabaseTaskRepository implements TaskRepository {

    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private final String url;

    public DatabaseTaskRepository() {
        this.url = resolveUrl(null);
        logInit();
        initDatabase();
    }

    public DatabaseTaskRepository(String customDbPath) {
        this.url = resolveUrl(customDbPath);
        logInit();
        initDatabase();
    }

    private String resolveUrl(String customPath) {
        if (System.getenv("GITHUB_ACTIONS") != null) {
            System.out.println("🏗 Running in GitHub Actions CI → using in-memory H2 database");
            return "jdbc:h2:mem:planit;DB_CLOSE_DELAY=-1";
        } else if (customPath != null) {
            System.out.println("🧪 Running test database at: " + customPath);
            return "jdbc:h2:file:" + customPath + ";AUTO_SERVER=TRUE";
        } else {
            String dbPath = System.getProperty("user.dir") + "/planit_db";
            System.out.println("💾 Running locally → using file-based H2 database");
            return "jdbc:h2:file:" + dbPath + ";AUTO_SERVER=TRUE";
        }
    }

    private void logInit() {
        System.out.println("🗂 DatabaseTaskRepository initialized");
        System.out.println("📁 H2 URL = " + url);
        System.out.println("📂 Working directory = " + System.getProperty("user.dir"));
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
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
                            updated_at TIMESTAMP,
                            group_id BIGINT
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
                            updated_at TIMESTAMP,
                            group_id BIGINT
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS groups (
                            id IDENTITY PRIMARY KEY,
                            name VARCHAR(255) NOT NULL
                        );
                    """);

            stmt.execute("""
                                                CREATE TABLE IF NOT EXISTS task_instances_completed (
                            id IDENTITY PRIMARY KEY,
                            task_id UUID NOT NULL,
                            completed_date DATE NOT NULL
                        );
                    """);

            try {
                stmt.execute("ALTER TABLE tasks ADD COLUMN repeat_frequency VARCHAR(20)");
            } catch (SQLException ignored) {
            }
            try {
                stmt.execute("ALTER TABLE tasks ADD COLUMN repeat_days VARCHAR(50)");
            } catch (SQLException ignored) {
            }
            try {
                stmt.execute("ALTER TABLE tasks ADD COLUMN repeat_until DATE");
            } catch (SQLException ignored) {
            }
            try {
                stmt.execute("ALTER TABLE tasks ADD COLUMN repeat_interval INT");
            } catch (SQLException ignored) {
            }

            try {
                stmt.execute("ALTER TABLE archive ADD COLUMN repeat_frequency VARCHAR(20)");
            } catch (SQLException ignored) {
            }
            try {
                stmt.execute("ALTER TABLE archive ADD COLUMN repeat_days VARCHAR(50)");
            } catch (SQLException ignored) {
            }
            try {
                stmt.execute("ALTER TABLE archive ADD COLUMN repeat_until DATE");
            } catch (SQLException ignored) {
            }
            try {
                stmt.execute("ALTER TABLE archive ADD COLUMN repeat_interval INT");
            } catch (SQLException ignored) {
            }

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
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                String title = rs.getString("title");
                LocalDate deadline = (rs.getDate("deadline") != null) ? rs.getDate("deadline").toLocalDate() : null;
                Priority priority = (rs.getString("priority") != null) ? Priority.valueOf(rs.getString("priority"))
                        : null;
                Long groupId = rs.getObject("group_id") != null ? rs.getLong("group_id") : null;

                boolean done = rs.getBoolean("done");
                boolean archived = rs.getBoolean("archived");
                LocalDateTime createdAt = rs.getTimestamp("created_at") != null
                        ? rs.getTimestamp("created_at").toLocalDateTime()
                        : LocalDateTime.now();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at") != null
                        ? rs.getTimestamp("updated_at").toLocalDateTime()
                        : LocalDateTime.now();
                String freqStr = rs.getString("repeat_frequency");
                RepeatFrequency freq = (freqStr != null) ? RepeatFrequency.valueOf(freqStr) : RepeatFrequency.NONE;

                String repeatDays = rs.getString("repeat_days");
                LocalDate repeatUntil = rs.getDate("repeat_until") != null
                        ? rs.getDate("repeat_until").toLocalDate()
                        : null;

                Integer repeatInterval = (Integer) rs.getObject("repeat_interval");

                Task t = new Task(
                        id, title, deadline, priority, groupId, done, archived,
                        createdAt, updatedAt,
                        freq, repeatDays, repeatUntil, repeatInterval);
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
                                    INSERT INTO %s (id, title, deadline, priority, group_id, done, archived,
                                created_at, updated_at,
                                repeat_frequency, repeat_days, repeat_until, repeat_interval)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                """.formatted(table);

        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD)) {
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
                    ps.setObject(5, t.getGroupId()); // 👈 Neu
                    ps.setBoolean(6, t.isDone());
                    ps.setBoolean(7, t.isArchived());
                    ps.setTimestamp(8, Timestamp.valueOf(t.getCreatedAt()));
                    ps.setTimestamp(9, Timestamp.valueOf(t.getUpdatedAt()));
                    ps.setString(10, t.getRepeatFrequency() != null ? t.getRepeatFrequency().name() : null);
                    ps.setString(11, t.getRepeatDays());
                    ps.setObject(12, t.getRepeatUntil());

                    if (t.getRepeatInterval() != null) {
                        ps.setInt(13, t.getRepeatInterval());
                    } else {
                        ps.setNull(13, Types.INTEGER);
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("⚠️ Error saving to " + table + ": " + e.getMessage());
        }
    }

}
