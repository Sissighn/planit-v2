package com.setayesh.planit.storage;

import com.setayesh.planit.core.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class DatabaseTaskRepositoryIntegrationTest {

    private static final String DB_PATH = System.getProperty("user.dir") + "/planit_test_db";
    private DatabaseTaskRepository repo;

    @BeforeEach
    void setup() {
        repo = new DatabaseTaskRepository(System.getProperty("user.dir") + "/planit_test_db");
    }

    @AfterEach
    void cleanup() {
        File dbFile = new File(System.getProperty("user.dir") + "/planit_test_db.mv.db");
        if (dbFile.exists())
            dbFile.delete();
    }

    @Test
    void databaseFileShouldExistAfterInit() {
        File dbFile = new File(DB_PATH + ".mv.db");

        if (System.getenv("GITHUB_ACTIONS") != null) {
            System.out.println("Skipping file existence check in CI");
            return;
        }

        assertTrue(dbFile.exists(), "Database file should exist after initialization");
    }

    @Test
    void saveAndReadTasks() {
        Task t1 = new Task("111", LocalDate.now(), Priority.HIGH);
        Task t2 = new Task("222", LocalDate.now().plusDays(1), Priority.MEDIUM);

        assertNull(t1.getTime());
        assertNull(t1.getExcludedDates());
        assertEquals(t1.getDeadline(), t1.getStartDate());

        repo.saveAll(List.of(t1, t2));

        List<Task> loaded = repo.findAll();
        assertEquals(2, loaded.size());

        Task l1 = loaded.stream()
                .filter(t -> t.getTitle().equals("111"))
                .findFirst()
                .orElseThrow();

        assertEquals(Priority.HIGH, l1.getPriority());
        assertNull(l1.getTime());
        assertNull(l1.getExcludedDates());
        assertEquals(l1.getDeadline(), l1.getStartDate());
    }

    @Test
    void saveAndReadArchive() {
        Task archived = new Task("Old Notes", LocalDate.now().minusDays(30), Priority.LOW);
        archived.setArchived(true);

        repo.saveArchive(List.of(archived));
        List<Task> archive = repo.loadArchive();

        assertEquals(1, archive.size());
        assertTrue(archive.get(0).isArchived());
        assertEquals(archive.get(0).getDeadline(), archive.get(0).getStartDate());
    }
}
