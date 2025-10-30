package com.setayesh.planit.ui;

import com.setayesh.planit.core.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input/output for PlanIt v2.
 */
public class ConsoleUI {
    private final TaskService service;
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ConsoleUI(TaskService service) {
        this.service = service;
    }

    public void start() {
        UIHelper.printHeader(UIHelper.t("welcome"));

        boolean running = true;
        while (running) {
            System.out.println();
            UIHelper.printPageHeader("home");

            List<Task> tasks = service.getAll();
            TodoPrinter.printTodoList(new ArrayList<>(tasks));
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addTask();
                case "2" -> editTask();
                case "3" -> toggleDone();
                case "4" -> deleteTask();
                case "5" -> archiveTask();
                case "6" -> viewArchive();
                case "7" -> clearCompleted();
                case "8" -> sortTasks();
                case "9" -> settingsMenu();
                case "10" -> {
                    System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("goodbye") + UIHelper.RESET);
                    service.save();
                    running = false;
                }
                default -> System.out.println(
                        String.format(UIHelper.t("invalid_choice"), 1, 10));
            }
        }
    }

    private void addTask() {
        System.out.print(UIHelper.t("enter_new"));
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println(UIHelper.PASTEL_RED + UIHelper.t("empty_task") + UIHelper.RESET);
            return;
        }

        System.out.print(UIHelper.t("add_deadline"));
        String dateStr = scanner.nextLine().trim();
        LocalDate deadline = null;
        if (!dateStr.isEmpty()) {
            try {
                deadline = LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (Exception e) {
                System.out.println(UIHelper.PASTEL_YELLOW + "Invalid date format, ignored." + UIHelper.RESET);
            }
        }

        System.out.print(UIHelper.t("add_priority"));
        String prioStr = scanner.nextLine().trim();
        Priority priority = null;
        if (!prioStr.isEmpty()) {
            switch (prioStr) {
                case "1" -> priority = Priority.HIGH;
                case "2" -> priority = Priority.MEDIUM;
                case "3" -> priority = Priority.LOW;
                default -> System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("invalid_priority") + UIHelper.RESET);
            }
        }

        service.addTask(new Task(title, deadline, priority));
        System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("task_added") + UIHelper.RESET);
    }

    private void editTask() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_tasks") + UIHelper.RESET);
            return;
        }

        TodoPrinter.printTodoList(new ArrayList<>(tasks));
        int index = askIndex("Enter number to edit: ", tasks.size());
        if (index == -1)
            return;

        System.out.print("New title (leave empty to keep): ");
        String title = scanner.nextLine().trim();

        System.out.print("New deadline (dd.MM.yyyy or empty): ");
        String dateStr = scanner.nextLine().trim();
        LocalDate deadline = null;
        if (!dateStr.isEmpty()) {
            try {
                deadline = LocalDate.parse(dateStr, FMT);
            } catch (Exception e) {
                System.out.println("Invalid date, ignored.");
            }
        }

        System.out.print("New priority (1=HIGH, 2=MEDIUM, 3=LOW, empty=keep): ");
        String prioStr = scanner.nextLine().trim();
        Priority priority = null;
        if (!prioStr.isEmpty()) {
            switch (prioStr) {
                case "1" -> priority = Priority.HIGH;
                case "2" -> priority = Priority.MEDIUM;
                case "3" -> priority = Priority.LOW;
                default -> System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("invalid_priority") + UIHelper.RESET);
            }
        }

        service.editTask(index, title, deadline, priority);
        System.out.println(UIHelper.PASTEL_GREEN + "Task updated." + UIHelper.RESET);
    }

    // 3) Mark / Unmark Done
    private void toggleDone() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_to_mark") + UIHelper.RESET);
            return;
        }

        TodoPrinter.printTodoList(new ArrayList<>(tasks));
        int index = askIndex("Enter number to toggle: ", tasks.size());
        if (index == -1)
            return;

        Task t = tasks.get(index);
        if (t.isDone())
            service.markUndone(index);
        else
            service.markDone(index);

        System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("marked_done") + UIHelper.RESET);
    }

    // 4) Delete Task
    private void deleteTask() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + "No tasks to delete." + UIHelper.RESET);
            return;
        }

        TodoPrinter.printTodoList(new ArrayList<>(tasks));
        int index = askIndex("Enter number to delete: ", tasks.size());
        if (index == -1)
            return;

        service.deleteTask(index);
        System.out.println(UIHelper.PASTEL_GREEN + "Task deleted." + UIHelper.RESET);
    }

    // 5) Archive Task
    private void archiveTask() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_tasks") + UIHelper.RESET);
            return;
        }

        TodoPrinter.printTodoList(new ArrayList<>(tasks));
        int index = askIndex("Enter number to archive (0 to cancel): ", tasks.size(), true);
        if (index == -1) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("deletion_cancel") + UIHelper.RESET);
            return;
        }

        service.archiveTask(index);
        System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("archived_success") + UIHelper.RESET);
    }

    // 6) View Archive
    private void viewArchive() {
        List<Task> archived = service.loadArchive();
        UIHelper.printPageHeader("viewArchive");
        if (archived.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("archive_empty") + UIHelper.RESET);
            return;
        }
        TodoPrinter.printTodoList(new ArrayList<>(archived));
        System.out.println(UIHelper.PASTEL_PURPLE + UIHelper.t("press_enter") + UIHelper.RESET);
        scanner.nextLine();
    }

    // 7) Clear Completed
    private void clearCompleted() {
        service.clearCompletedNotArchived();
        System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("tasks_cleared") + UIHelper.RESET);
    }

    // 8) Sort
    private void sortTasks() {
        System.out.println("""
                Sort by:
                1) Deadline
                2) Priority
                3) Title
                """);
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> service.sortByDeadline();
            case "2" -> service.sortByPriority();
            case "3" -> service.sortByTitle();
            default -> System.out.println(UIHelper.PASTEL_YELLOW + "Invalid choice." + UIHelper.RESET);
        }
        System.out.println(UIHelper.PASTEL_GREEN + "Tasks sorted." + UIHelper.RESET);
    }

    // Settings
    private void settingsMenu() {
        UIHelper.printPageHeader("settings");
        System.out.println(UIHelper.PASTEL_YELLOW + "Settings coming soon!" + UIHelper.RESET);
        System.out.println(UIHelper.PASTEL_PURPLE + UIHelper.t("press_enter") + UIHelper.RESET);
        scanner.nextLine();
    }

    // Helper methods
    private int askIndex(String prompt, int size) {
        return askIndex(prompt, size, false);
    }

    private int askIndex(String prompt, int size, boolean allowZeroCancel) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (allowZeroCancel && n == 0)
                    return -1;
                int idx = n - 1;
                if (idx >= 0 && idx < size)
                    return idx;
            } catch (NumberFormatException ignored) {
            }
            System.out.println(UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
        }
    }
}
