package com.setayesh.planit.core;

import com.setayesh.planit.storage.InMemoryTaskRepository;
import com.setayesh.planit.storage.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(1, all.size());
        assertEquals("Test feature", all.get(0).getTitle());
        assertFalse(all.get(0).isDone());
        assertNull(all.get(0).getStartDate());
    }

    @Test
    void markDoneByIdShouldPersist() {
        Task t = new Task("Finish planit refactor");
        service.addTask(t);

        service.markDone(t.getId());

        assertTrue(repo.findAll().get(0).isDone());
    }

    @Test
    void deleteByIdShouldRemoveTask() {
        Task t = new Task("Temporary");
        service.addTask(t);

        service.deleteTask(t.getId());

        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void newFieldsShouldPersistInRepo() {
        Task t = new Task("Yoga");
        t.setTime("09:30");
        t.addExcludedDate(LocalDate.of(2025, 2, 1));

        service.addTask(t);

        Task loaded = repo.findAll().get(0);

        assertEquals("09:30", loaded.getTime());
        assertEquals("2025-02-01", loaded.getExcludedDates());
    }
}
