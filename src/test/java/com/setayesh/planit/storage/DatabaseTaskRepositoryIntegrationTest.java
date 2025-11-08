package com.setayesh.planit.storage;

import com.setayesh.planit.core.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTaskRepositoryIntegrationTest {

    private static final String DB_PATH = System.getProperty("user.dir") + "/planit_db";
    private DatabaseTaskRepository repo;

    @BeforeEach
    void setup() {
        repo = new DatabaseTaskRepository();
    }

    @Test
    void databaseFileShouldExistAfterInit() {
        File dbFile = new File(DB_PATH + ".mv.db");
        assertTrue(dbFile.exists(), "Database file should exist after initialization");
    }

    @Test
    void saveAndReadTasks() {
        Task t1 = new Task("111", LocalDate.now(), Priority.HIGH);
        Task t2 = new Task("222", LocalDate.now().plusDays(1), Priority.MEDIUM);

        List<Task> tasks = List.of(t1, t2);
        repo.saveAll(tasks);

        List<Task> loaded = repo.findAll();
        assertEquals(2, loaded.size(), "Should read back two tasks");
        assertTrue(loaded.stream().anyMatch(t -> t.getTitle().equals("111")));
        assertTrue(loaded.stream().anyMatch(t -> t.getPriority() == Priority.MEDIUM));
    }

    @Test
    void saveAndReadArchive() {
        Task archived = new Task("Old Notes", LocalDate.now().minusDays(30), Priority.LOW);
        archived.setArchived(true);

        repo.saveArchive(List.of(archived));
        List<Task> archive = repo.loadArchive();

        assertEquals(1, archive.size(), "Should load one archived task");
        assertTrue(archive.get(0).isArchived(), "Task should be marked as archived");
    }
}
