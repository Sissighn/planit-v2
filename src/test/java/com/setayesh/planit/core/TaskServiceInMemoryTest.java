package com.setayesh.planit.core;

import com.setayesh.planit.storage.InMemoryTaskRepository;
import com.setayesh.planit.storage.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style unit test that verifies TaskService
 * works correctly when backed by an InMemoryTaskRepository.
 */
class TaskServiceInMemoryTest {

    private TaskRepository repo;
    private TaskService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTaskRepository();
        service = new TaskService(repo);
    }

    @Test
    void addAndRetrieveTask() {
        Task t1 = new Task("Test feature");
        service.addTask(t1);

        List<Task> all = service.getAll();
        assertEquals(1, all.size(), "Expected exactly one task after add()");
        assertEquals("Test feature", all.get(0).getTitle());
        assertFalse(all.get(0).isDone(), "New task should not be marked done by default");
    }

    @Test
    void toggleDoneShouldPersist() {
        Task t1 = new Task("Toggle me");
        service.addTask(t1);

        service.markDone(0); // flip done state
        assertTrue(repo.findAll().get(0).isDone(), "Repository should reflect done state after toggle");
    }

    @Test
    void deleteRemovesTaskFromRepo() {
        Task t1 = new Task("Temp");
        service.addTask(t1);

        // Suppose you add a delete(index) method in service later
        // For now, simulate removal manually
        List<Task> current = repo.findAll();
        current.remove(0);
        repo.saveAll(current);

        assertEquals(0, repo.findAll().size(), "Task should be removed from repo");
    }
}
