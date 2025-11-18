package com.setayesh.planit.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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

        // startDate is NULL unless explicitly set
        assertNull(t.getStartDate());
    }

    @Test
    void defaultConstructor_shouldAllowManualSetters() {
        Task t = new Task();
        t.setTitle("Study Java");
        t.setDeadline(LocalDate.of(2025, 12, 24));
        t.setPriority(Priority.MEDIUM);

        assertEquals("Study Java", t.getTitle());
        assertEquals(LocalDate.of(2025, 12, 24), t.getDeadline());

        // startDate remains null unless explicitly set
        assertNull(t.getStartDate());

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
        assertTrue(str.startsWith("[ ]"));

        t.markDone();
        assertTrue(t.toString().startsWith("[✔]"));

        t.setArchived(true);
        assertTrue(t.toString().contains("{archived}"));
    }

    @Test
    void newFields_shouldBeNullByDefault() {
        Task t = new Task("Test", LocalDate.now(), Priority.HIGH);

        assertNull(t.getTime());
        assertNull(t.getExcludedDates());
        assertNull(t.getStartDate());
    }

    @Test
    void addExcludedDate_shouldAppendCorrectly() {
        Task t = new Task("Repeat", null, Priority.MEDIUM);
        t.addExcludedDate(LocalDate.of(2025, 1, 1));
        t.addExcludedDate(LocalDate.of(2025, 1, 2));

        assertEquals("2025-01-01,2025-01-02", t.getExcludedDates());
    }

    @Test
    void setTime_shouldStoreTimeString() {
        Task t = new Task("Meeting", null, Priority.HIGH);
        t.setTime("14:00");

        assertEquals("14:00", t.getTime());
    }

    @Test
    void startDate_shouldBeSetOnlyWhenExplicitlyProvided() {
        Task t = new Task("Study", LocalDate.of(2025, 10, 10), Priority.LOW);

        assertNull(t.getStartDate());

        t.setStartDate(LocalDate.of(2025, 10, 10));
        assertEquals(LocalDate.of(2025, 10, 10), t.getStartDate());
    }
}
