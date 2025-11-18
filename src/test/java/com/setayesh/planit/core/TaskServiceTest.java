package com.setayesh.planit.core;

import com.setayesh.planit.storage.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

        // startDate should remain null
        assertNull(all.get(0).getStartDate());
    }

    @Test
    void excludeDate_shouldMarkSingleOccurrenceRemoved() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        Task t = new Task("Gym", LocalDate.of(2025, 1, 1), Priority.HIGH);
        t.setRepeatFrequency(RepeatFrequency.DAILY);

        service.addTask(t);

        // Excluded dates stored in Task itself → no instanceRepo needed for tests
        service.excludeDate(t.getId(), LocalDate.of(2025, 1, 2));

        assertTrue(
                service.findById(t.getId()).get()
                        .getExcludedDates().contains("2025-01-02"));
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
    void markDone_shouldSetDoneTrue_whenOneTimeTask() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var task = new Task("Finish homework", null, Priority.LOW);
        service.addTask(task);

        service.markDone(task.getId());
        assertTrue(service.findById(task.getId()).orElseThrow().isDone());
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

    @Test
    void archiveTask_shouldMoveToArchiveList() {
        var repo = new InMemoryTaskRepository();
        var service = new TaskService(repo);

        var t = new Task("Archive Me", null, Priority.MEDIUM);
        service.addTask(t);

        service.archiveTask(t.getId());

        assertTrue(repo.loadArchive().stream()
                .anyMatch(a -> a.getTitle().equals("Archive Me")));

        assertTrue(service.getAll().isEmpty());
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
        assertTrue(remaining.stream().anyMatch(Task::isArchived));
    }
}
