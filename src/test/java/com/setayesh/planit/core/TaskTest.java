package com.setayesh.planit.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void defaultConstructor_shouldInitializeFields() {
        Task t = new Task("Test");

        assertNotNull(t.getId());
        assertEquals("Test", t.getTitle());
        assertFalse(t.isDone());
        assertFalse(t.isArchived());
        assertEquals(RepeatFrequency.NONE, t.getRepeatFrequency());
    }

    @Test
    void markDone_shouldSetDoneTrue() {
        Task t = new Task("Homework");
        assertFalse(t.isDone());
        t.markDone();
        assertTrue(t.isDone());
    }

    @Test
    void markUndone_shouldUnsetDone() {
        Task t = new Task("Homework");
        t.markDone();
        assertTrue(t.isDone());
        t.markUndone();
        assertFalse(t.isDone());
    }

    @Test
    void toString_shouldContainTitleAndPriority() {
        Task t = new Task("Do homework", LocalDate.of(2025, 1, 1), Priority.HIGH);

        String s = t.toString();

        assertTrue(s.contains("Do homework"));
        assertTrue(s.contains("HIGH"));
    }

    @Test
    void toString_shouldShowCheckmarkForDone() {
        Task t = new Task("Do homework");
        t.markDone();

        String s = t.toString();
        assertTrue(s.contains("[✔]"));
    }

    @Test
    void toString_shouldShowArchivedFlag() {
        Task t = new Task("Archived");
        t.setArchived(true);

        String s = t.toString();

        assertTrue(s.contains("{archived}"));
    }

    @Test
    void toString_shouldShowDeadlineIfPresent() {
        Task t = new Task("Due Task", LocalDate.of(2025, 5, 20), Priority.MEDIUM);

        String s = t.toString();

        assertTrue(s.contains("due: 2025-05-20"));
    }

    @Test
    void toString_shouldShowRepeatFrequency() {
        Task t = new Task("Repeat Daily");
        t.setRepeatFrequency(RepeatFrequency.DAILY);

        String s = t.toString();

        assertTrue(s.contains("repeats: DAILY"));
    }

    @Test
    void settersShouldUpdateUpdatedAt() throws InterruptedException {
        Task t = new Task("Test");

        LocalDate initialUpdate = t.getUpdatedAt().toLocalDate();

        Thread.sleep(10);
        t.setTitle("New Title");

        assertTrue(t.getUpdatedAt().isAfter(t.getCreatedAt()));
    }
}
