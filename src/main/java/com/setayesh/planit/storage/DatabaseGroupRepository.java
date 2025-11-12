package com.setayesh.planit.storage;

import com.setayesh.planit.core.Group;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DatabaseGroupRepository {

    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private final String url;

    public DatabaseGroupRepository() {
        this.url = resolveUrl(null);
        initSchema();
    }

    public DatabaseGroupRepository(String customDbPath) {
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
                        CREATE TABLE IF NOT EXISTS groups (
                            id IDENTITY PRIMARY KEY,
                            name VARCHAR(255) NOT NULL
                        );
                    """);

            // Optional (nur wenn du FK möchtest; H2 braucht existierende Spalten):
            // stmt.execute("ALTER TABLE tasks ADD CONSTRAINT IF NOT EXISTS fk_tasks_group
            // FOREIGN KEY (group_id) REFERENCES groups(id);");

        } catch (SQLException e) {
            System.err.println("⚠️ Error initializing groups schema: " + e.getMessage());
        }
    }

    public List<Group> findAll() {
        List<Group> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT id,name FROM groups ORDER BY name ASC")) {
            while (rs.next())
                list.add(new Group(rs.getLong("id"), rs.getString("name")));
        } catch (SQLException e) {
            System.err.println("⚠️ Error reading groups: " + e.getMessage());
        }
        return list;
    }

    public Group save(Group g) {
        if (g.getId() == null)
            return insert(g);
        return update(g);
    }

    private Group insert(Group g) {
        String sql = "INSERT INTO groups (name) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    g.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error inserting group: " + e.getMessage());
        }
        return g;
    }

    private Group update(Group g) {
        String sql = "UPDATE groups SET name=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getName());
            ps.setLong(2, g.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠️ Error updating group: " + e.getMessage());
        }
        return g;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM groups WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠️ Error deleting group: " + e.getMessage());
        }
    }
}
