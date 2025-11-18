package com.setayesh.planit.storage;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.Priority;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTaskRepositoryIntegrationTest {

    private static final String DB_PATH = System.getProperty("user.dir") + "/planit_db.mv.db";

    private DatabaseTaskRepository repo;

    @BeforeEach
    void setup() {
        repo = new DatabaseTaskRepository(); // <-- FIX
    }

    @AfterEach
    void cleanup() {
        File dbFile = new File(DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    void databaseFileShouldExistAfterInit() {
        if (System.getenv("GITHUB_ACTIONS") != null) {
            // In CI: in-memory DB → kein File.
            return;
        }

        File dbFile = new File(DB_PATH);
        assertTrue(dbFile.exists(), "Database file should exist after initialization");
    }

    @Test
    void saveAndReadTasks() {
        Task t1 = new Task("111", LocalDate.now(), Priority.HIGH);
        Task t2 = new Task("222", LocalDate.now().plusDays(1), Priority.MEDIUM);

        repo.saveAll(List.of(t1, t2));

        List<Task> loaded = repo.findAll();
        assertEquals(2, loaded.size());

        Task l1 = loaded.stream()
                .filter(t -> t.getTitle().equals("111"))
                .findFirst()
                .orElseThrow();

        assertEquals(Priority.HIGH, l1.getPriority());
        assertNull(l1.getStartDate());
    }

    @Test
    void saveAndReadArchive() {
        Task archived = new Task("Old Notes", LocalDate.now().minusDays(30), Priority.LOW);
        archived.setArchived(true);

        repo.saveArchive(List.of(archived));
        List<Task> archive = repo.loadArchive();

        assertEquals(1, archive.size());
        assertTrue(archive.get(0).isArchived());
        assertNull(archive.get(0).getStartDate());
    }
}
