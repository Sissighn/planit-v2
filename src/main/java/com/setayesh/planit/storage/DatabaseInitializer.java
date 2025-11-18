package com.setayesh.planit.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class DatabaseInitializer {

    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private final String url;

    public DatabaseInitializer() {
        String dbPath = System.getProperty("user.dir") + "/planit_db";
        this.url = "jdbc:h2:file:" + dbPath + ";AUTO_SERVER=TRUE";
        System.out.println("🔧 DatabaseInitializer using URL = " + url);
    }

    @PostConstruct
    public void init() {
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            System.out.println("🔨 Initializing all database tables...");

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
                            group_id BIGINT,
                            repeat_frequency VARCHAR(20),
                            repeat_days VARCHAR(50),
                            repeat_until DATE,
                            repeat_interval INT,
                            time VARCHAR(20),
                            excluded_dates VARCHAR(500),
                            start_date DATE
                        );
                    """);

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
                            group_id BIGINT,
                            repeat_frequency VARCHAR(20),
                            repeat_days VARCHAR(50),
                            repeat_until DATE,
                            repeat_interval INT,
                            time VARCHAR(20),
                            excluded_dates VARCHAR(500),
                            start_date DATE
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

            System.out.println("✅ All tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("❌ DB initialization error: " + e.getMessage());
        }
    }
}
