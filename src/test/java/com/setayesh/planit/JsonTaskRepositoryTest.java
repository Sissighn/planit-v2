package com.setayesh.planit;

import com.setayesh.planit.storage.JsonTaskRepository;
import com.setayesh.planit.storage.TaskRepository;
import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.Priority;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests for JsonTaskRepository using a temporary directory.
 */
class JsonTaskRepositoryTest {

    private Path tempDir;
    private TaskRepository repo;

    @BeforeEach
    void setup() throws IOException {
        // Create a unique temporary directory for each test
        tempDir = Files.createTempDirectory("planit_test_");
        repo = new JsonTaskRepository(tempDir.toString());
    }

    @AfterEach
    void cleanup() throws IOException {
        // Delete the entire temp folder after each test
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a)) // delete files before dirs
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Test
    void saveAndLoad_shouldPersistTasksCorrectly() {
        Task t1 = new Task("Task 1", LocalDate.now(), Priority.HIGH);
        Task t2 = new Task("Task 2", LocalDate.now().plusDays(1), Priority.LOW);

        repo.saveAll(List.of(t1, t2));

        List<Task> loaded = repo.findAll();

        assertEquals(2, loaded.size());
        assertEquals("Task 1", loaded.get(0).getTitle());
        assertEquals("Task 2", loaded.get(1).getTitle());
    }

    @Test
    void saveArchiveAndLoadArchive_shouldWorkIndependently() {
        Task archived = new Task("Archived Task", null, Priority.MEDIUM);
        archived.setArchived(true);

        repo.saveArchive(List.of(archived));

        List<Task> loadedArchive = repo.loadArchive();
        assertEquals(1, loadedArchive.size());
        assertTrue(loadedArchive.get(0).isArchived());
        assertEquals("Archived Task", loadedArchive.get(0).getTitle());
    }

    @Test
    void load_shouldReturnEmptyListWhenFileMissing() {
        List<Task> loaded = repo.findAll();
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty(), "Missing file should produce empty list");
    }

    @Test
    void load_shouldCreateBackupOnCorruptFile() throws IOException {
        File badFile = new File(tempDir.toFile(), "planit_tasks.json");
        Files.writeString(badFile.toPath(), "{ invalid json");

        List<Task> result = repo.findAll();

        assertTrue(result.isEmpty(), "Corrupt file should result in empty list");
        File backup = new File(tempDir.toFile(), "planit_tasks.json.corrupted");
        assertTrue(backup.exists(), "Corrupted backup file should be created");
    }

    @Test
    void write_shouldReplaceOldFileWithTemp() throws IOException {
        // Erstes Speichern
        Task t = new Task("Temp Test", null, Priority.LOW);
        repo.saveAll(List.of(t));

        File mainFile = new File(tempDir.toFile(), "planit_tasks.json");
        File tempFile = new File(tempDir.toFile(), "planit_tasks.json.tmp");

        assertTrue(mainFile.exists(), "Main file should exist after save");
        assertFalse(tempFile.exists(), "Temporary file should be cleaned up");
    }
}
