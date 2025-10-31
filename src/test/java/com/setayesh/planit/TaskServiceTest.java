package com.setayesh.planit;

import com.setayesh.planit.core.TaskService;
import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.Priority;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TaskService using an in-memory repository.
 */
class TaskServiceTest {

    @Test
    void addTask_shouldAddToList() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var task = new Task("Learn Java", LocalDate.now(), Priority.HIGH);
        service.addTask(task);

        assertEquals(1, service.size());
        assertEquals("Learn Java", service.get(0).getTitle());
    }

    @Test
    void deleteTask_shouldRemoveByIndex() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var t1 = new Task("Task 1", null, Priority.MEDIUM);
        var t2 = new Task("Task 2", null, Priority.LOW);
        service.addTask(t1);
        service.addTask(t2);

        service.deleteTask(0);
        assertEquals(1, service.size());
        assertEquals("Task 2", service.get(0).getTitle());
    }

    @Test
    void markDone_shouldSetDoneTrue() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var task = new Task("Finish homework", null, Priority.LOW);
        service.addTask(task);

        service.markDone(0);
        assertTrue(service.get(0).isDone());
    }

    @Test
    void editTask_shouldUpdateTitleAndPriority() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var task = new Task("Old Title", null, Priority.LOW);
        service.addTask(task);

        service.editTask(0, "New Title", LocalDate.of(2025, 11, 1), Priority.HIGH);

        var updated = service.get(0);
        assertEquals("New Title", updated.getTitle());
        assertEquals(LocalDate.of(2025, 11, 1), updated.getDeadline());
        assertEquals(Priority.HIGH, updated.getPriority());
    }

    @Test
    void sortByTitle_shouldSortAlphabetically() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        service.addTask(new Task("Zebra", null, Priority.LOW));
        service.addTask(new Task("Apple", null, Priority.HIGH));
        service.sortByTitle();

        assertEquals("Apple", service.get(0).getTitle());
        assertEquals("Zebra", service.get(1).getTitle());
    }
}
