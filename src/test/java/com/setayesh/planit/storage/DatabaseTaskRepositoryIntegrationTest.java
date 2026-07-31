package com.setayesh.planit.storage;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.Priority;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTaskRepositoryIntegrationTest {

    @TempDir
    Path tempDir;

    private Path databaseFile;
    private DatabaseTaskRepository repo;

    @BeforeEach
    void setup() {
        Path databasePath = tempDir.resolve("planit-test");
        databaseFile = Path.of(databasePath + ".mv.db");
        repo = new DatabaseTaskRepository(databasePath.toString());
    }

    @Test
    void databaseFileShouldExistAfterInit() {
        assertTrue(Files.exists(databaseFile), "Database file should exist after initialization");
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
