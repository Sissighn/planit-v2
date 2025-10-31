package com.setayesh.planit;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.Priority;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Task entity.
 */
class TaskTest {

    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        LocalDate date = LocalDate.of(2025, 11, 1);
        Task t = new Task("Write code", date, Priority.HIGH);

        assertEquals("Write code", t.getTitle());
        assertEquals(date, t.getDeadline());
        assertEquals(Priority.HIGH, t.getPriority());
        assertFalse(t.isDone());
        assertFalse(t.isArchived());
    }

    @Test
    void defaultConstructor_shouldAllowManualSetters() {
        Task t = new Task();
        t.setTitle("Study Java");
        t.setDeadline(LocalDate.of(2025, 12, 24));
        t.setPriority(Priority.MEDIUM);

        assertEquals("Study Java", t.getTitle());
        assertEquals(LocalDate.of(2025, 12, 24), t.getDeadline());
        assertEquals(Priority.MEDIUM, t.getPriority());
    }

    @Test
    void markDoneAndUndone_shouldToggleDoneFlag() {
        Task t = new Task("Test toggles", null, Priority.LOW);

        assertFalse(t.isDone());
        t.markDone();
        assertTrue(t.isDone());
        t.markUndone();
        assertFalse(t.isDone());
    }

    @Test
    void archiveFlag_shouldBeSettable() {
        Task t = new Task("Archive test", null, Priority.MEDIUM);

        assertFalse(t.isArchived());
        t.setArchived(true);
        assertTrue(t.isArchived());
    }

    @Test
    void toString_shouldReflectStatusAndFields() {
        LocalDate date = LocalDate.of(2025, 10, 31);
        Task t = new Task("Halloween", date, Priority.HIGH);

        String str = t.toString();
        assertTrue(str.contains("Halloween"));
        assertTrue(str.contains("HIGH"));
        assertTrue(str.contains("due: 2025-10-31"));
        assertTrue(str.startsWith("[ ]"), "Should show unchecked when not done");

        t.markDone();
        String doneStr = t.toString();
        assertTrue(doneStr.startsWith("[✔]"), "Should show checked when done");

        t.setArchived(true);
        String archivedStr = t.toString();
        assertTrue(archivedStr.contains("{archived}"), "Should indicate archived status");
    }
}
