package com.setayesh.planit.core;

import com.setayesh.planit.storage.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

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

        var all = service.getAll();
        assertEquals(1, all.size());
        assertEquals("Learn Java", all.get(0).getTitle());
    }

    @Test
    void excludeDate_shouldMarkSingleOccurrenceRemoved() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        Task t = new Task("Gym", LocalDate.of(2025, 1, 1), Priority.HIGH);
        t.setRepeatFrequency(RepeatFrequency.DAILY);

        service.addTask(t);

        service.findById(t.getId()).get().addExcludedDate(LocalDate.of(2025, 1, 2));
        service.save();

        assertTrue(service.findById(t.getId()).get().getExcludedDates().contains("2025-01-02"));
    }

    @Test
    void deleteTask_shouldRemoveById() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var t1 = new Task("Task 1", null, Priority.MEDIUM);
        var t2 = new Task("Task 2", null, Priority.LOW);
        service.addTask(t1);
        service.addTask(t2);

        service.deleteTask(t1.getId());
        var remaining = service.getAll();

        assertEquals(1, remaining.size());
        assertEquals("Task 2", remaining.get(0).getTitle());
    }

    @Test
    void markDone_shouldSetDoneTrue() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var task = new Task("Finish homework", null, Priority.LOW);
        service.addTask(task);

        service.markDone(task.getId());

        var stored = service.findById(task.getId()).orElseThrow();
        assertTrue(stored.isDone());
    }

    @Test
    void editTask_shouldUpdateTitleAndPriority() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var task = new Task("Old Title", null, Priority.LOW);
        service.addTask(task);

        UUID id = task.getId();
        service.editTask(id, "New Title", LocalDate.of(2025, 11, 1), Priority.HIGH);

        var updated = service.findById(id).orElseThrow();
        assertEquals("New Title", updated.getTitle());
        assertEquals(LocalDate.of(2025, 11, 1), updated.getDeadline());
        assertEquals(Priority.HIGH, updated.getPriority());
    }

    void sortByTitle_shouldSortAlphabetically() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        service.addTask(new Task("Zebra", null, Priority.LOW));
        service.addTask(new Task("Apple", null, Priority.HIGH));

        service.sortByTitle();
        var all = service.getAll();

        assertEquals("Apple", all.get(0).getTitle());
        assertEquals("Zebra", all.get(1).getTitle());
    }

    @Test
    void archiveTask_shouldMoveToArchiveList() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var t = new Task("Archive Me", null, Priority.MEDIUM);
        service.addTask(t);

        service.archiveTask(t.getId());

        assertTrue(repo.loadArchive().stream()
                .anyMatch(a -> a.getTitle().equals("Archive Me")));
        assertTrue(service.getAll().isEmpty(), "Task should be removed from active list");
    }

    @Test
    void clearCompletedNotArchived_shouldRemoveOnlyCompletedNonArchivedTasks() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var done = new Task("Done Task", null, Priority.LOW);
        done.markDone();

        var active = new Task("Active Task", null, Priority.LOW);

        var archivedDone = new Task("Archived Task", null, Priority.MEDIUM);
        archivedDone.markDone();
        archivedDone.setArchived(true);

        service.addTask(done);
        service.addTask(active);
        service.addTask(archivedDone);

        service.clearCompletedNotArchived();

        var remaining = service.getAll();
        assertEquals(2, remaining.size());
        assertTrue(remaining.stream().anyMatch(t -> t.getTitle().equals("Active Task")));
        assertTrue(remaining.stream().anyMatch(Task::isArchived),
                "Archived done tasks should remain");
    }

    @Test
    void addTask_shouldPersistInMemoryRepo() {
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        TaskService service = new TaskService(repo);

        Task t = new Task("Test Task", LocalDate.now(), Priority.HIGH);
        service.addTask(t);

        assertEquals(1, repo.findAll().size());
        assertEquals("Test Task", repo.findAll().get(0).getTitle());
    }

}
