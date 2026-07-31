package com.setayesh.planit.ui;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.Priority;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TodoPrinter console output logic.
 * Focuses on text structure, not color/ANSI formatting.
 */
class TodoPrinterTest {

    private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    @BeforeEach
    void redirectOutput() {
        System.setOut(new PrintStream(output));
        output.reset();
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    void printTodoList_shouldPrintMessageWhenListEmpty() {
        TodoPrinter.printTodoList(List.of());
        String console = output.toString();
        assertTrue(console.contains("No tasks yet"), "Should show empty list message");
    }

    @Test
    void printTodoList_shouldPrintTasksInTableFormat() {
        Task t1 = new Task("Buy milk", LocalDate.of(2025, 11, 1), Priority.HIGH);
        Task t2 = new Task("Read book", LocalDate.of(2025, 11, 2), Priority.MEDIUM);

        TodoPrinter.printTodoList(List.of(t1, t2));

        String console = output.toString();

        // Grundstruktur prüfen
        assertTrue(console.contains("Task"), "Header should contain 'Task'");
        assertTrue(console.contains("Buy milk"), "First task should appear");
        assertTrue(console.contains("Read book"), "Second task should appear");
        assertTrue(console.contains("HIGH"), "Priority HIGH should appear");
        assertTrue(console.contains("MEDIUM"), "Priority MEDIUM should appear");
        assertTrue(console.contains("01.11.2025"), "Deadline formatted as dd.MM.yyyy");
    }

    @Test
    void printTodoList_shouldHandleLongTitlesGracefully() {
        String longTitle = "This title is intentionally very long and should be clipped visually at some point";
        Task t = new Task(longTitle, null, Priority.LOW);

        TodoPrinter.printTodoList(List.of(t));
        String console = output.toString();

        // Titel sollte gekürzt (mit "...") werden
        assertTrue(console.contains("..."), "Long title should be truncated with ellipsis");
    }

    @Test
    void printTodoList_shouldShowCheckmarkForDoneTasks() {
        Task doneTask = new Task("Finish project", null, Priority.HIGH);
        doneTask.markDone();

        TodoPrinter.printTodoList(List.of(doneTask));
        String console = output.toString();

        assertTrue(console.contains("✔"), "Done task should display checkmark symbol");
    }

    @Test
    void printTodoList_shouldUsePlaceholderForNullPriorityAndDeadline() {
        Task t = new Task("Untitled", null, null);

        TodoPrinter.printTodoList(List.of(t));
        String console = output.toString();

        // Ignoriere ANSI-Farbcodes und prüfe nur logischen Inhalt
        String cleaned = console.replaceAll("\u001B\\[[;\\d]*m", "");

        assertTrue(cleaned.contains("(—)"), "Should display dash for missing priority");
        assertTrue(cleaned.contains("—"), "Should display dash for missing deadline");
    }
}
