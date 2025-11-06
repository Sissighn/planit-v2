package com.setayesh.planit.ui;

import com.setayesh.planit.core.*;
import java.time.LocalDate;
import java.util.List;

public class MenuManager {
    private final TaskService service;
    private final InputHandler input;

    public MenuManager(TaskService service, InputHandler input) {
        this.service = service;
        this.input = input;
    }

    public boolean handleMainMenu(String choice) {
        return switch (choice) {
            case "1" -> {
                addTask();
                yield true;
            }
            case "2" -> {
                editTask();
                yield true;
            }
            case "3" -> {
                toggleDone();
                yield true;
            }
            case "4" -> {
                deleteTask();
                yield true;
            }
            case "5" -> {
                archiveTask();
                yield true;
            }
            case "6" -> {
                viewArchive();
                yield true;
            }
            case "7" -> {
                clearCompleted();
                yield true;
            }
            case "8" -> {
                sortTasks();
                yield true;
            }
            case "9" -> {
                settingsMenu();
                yield true;
            }
            case "10" -> false; // exit
            default -> {
                System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("invalid_choice") + Colors.RESET);
                yield true;
            }
        };
    }

    private void addTask() {
        String title = input.readNonEmpty(UIHelper.t("enter_new"));
        LocalDate deadline = input.readDate(UIHelper.t("add_deadline"));
        Priority priority = input.readPriority(UIHelper.t("add_priority"));

        service.addTask(new Task(title, deadline, priority));
        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("task_added") + Colors.RESET);
    }

    private void editTask() {
        UIHelper.printHeader(UIHelper.t("edit_title"));

        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("no_tasks") + Colors.RESET);
            return;
        }

        TodoPrinter.printTodoList(tasks);
        int index = input.readIndex(UIHelper.t("edit_choose"), tasks.size(), false);
        if (index == -1)
            return;

        String title = input.readLine("New title (empty = keep): ");
        LocalDate deadline = input.readDate("New deadline (dd.MM.yyyy or empty): ");
        Priority priority = input.readPriority("New priority (1=HIGH, 2=MEDIUM, 3=LOW, empty=keep): ");

        service.editTask(index, title, deadline, priority);
        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("task_updated") + Colors.RESET);
    }

    private void toggleDone() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("no_to_mark") + Colors.RESET);
            return;
        }

        TodoPrinter.printTodoList(tasks);
        int index = input.readIndex("Enter number to toggle: ", tasks.size(), false);
        if (index == -1)
            return;

        Task t = tasks.get(index);
        if (t.isDone())
            service.markUndone(index);
        else
            service.markDone(index);

        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("marked_done") + Colors.RESET);
    }

    private void deleteTask() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("no_tasks") + Colors.RESET);
            return;
        }

        TodoPrinter.printTodoList(tasks);
        int index = input.readIndex("Enter number to delete: ", tasks.size(), false);
        if (index == -1)
            return;

        service.deleteTask(index);
        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("task_deleted") + Colors.RESET);
    }

    private void archiveTask() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("no_tasks") + Colors.RESET);
            return;
        }

        TodoPrinter.printTodoList(tasks);
        int index = input.readIndex("Enter number to archive (0 = cancel): ", tasks.size(), true);
        if (index == -1) {
            System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("deletion_cancel") + Colors.RESET);
            return;
        }

        service.archiveTask(index);
        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("archived_success") + Colors.RESET);
    }

    private void viewArchive() {
        List<Task> archived = service.loadArchive();
        UIHelper.printPageHeader("viewArchive");

        if (archived.isEmpty()) {
            System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("archive_empty") + Colors.RESET);
            return;
        }

        TodoPrinter.printTodoList(archived);
        input.waitForEnter();
    }

    private void clearCompleted() {
        service.clearCompletedNotArchived();
        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("tasks_cleared") + Colors.RESET);
    }

    private void sortTasks() {
        System.out.println("""
                Sort by:
                1) Deadline
                2) Priority
                3) Title
                """);
        String choice = input.readLine("> ");
        switch (choice) {
            case "1" -> service.sortByDeadline();
            case "2" -> service.sortByPriority();
            case "3" -> service.sortByTitle();
            default -> System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("invalid_choice_simple") + Colors.RESET);
        }
        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("tasks_sorted") + Colors.RESET);
    }

    private void settingsMenu() {
        while (true) {
            UIHelper.printPageHeader("settings");
            System.out.println("""
                    1) Change language
                    2) Dashboard view
                    3) Back
                    """);
            String opt = input.readLine("> ");
            switch (opt) {
                case "1" -> changeLanguage();
                case "2" -> changeDashboardView();
                case "3" -> {
                    return;
                }
                default ->
                    System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("invalid_choice_simple") + Colors.RESET);
            }
        }
    }

    private void changeLanguage() {
        System.out.println("1) English\n2) Deutsch\n0) Cancel");
        String langChoice = input.readLine("> ");
        switch (langChoice) {
            case "1" -> UIHelper.setLanguage(UIHelper.Language.EN);
            case "2" -> UIHelper.setLanguage(UIHelper.Language.DE);
            case "0" -> {
                System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("deletion_cancel") + Colors.RESET);
                return;
            }
            default -> System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("invalid_choice_simple") + Colors.RESET);
        }
        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("language_changed") + Colors.RESET);
        input.waitForEnter();
    }

    private void changeDashboardView() {
        System.out.println("""
                1) Counts only
                2) Percentages only
                3) Both
                """);
        String choice = input.readLine("> ");
        switch (choice) {
            case "1" -> UIHelper.setDashboardMode(UIHelper.DashboardMode.COUNTS);
            case "2" -> UIHelper.setDashboardMode(UIHelper.DashboardMode.PERCENTAGES);
            case "3" -> UIHelper.setDashboardMode(UIHelper.DashboardMode.BOTH);
            default -> System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("invalid_choice_simple") + Colors.RESET);
        }
        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("dashboard_updated") + Colors.RESET);
        input.waitForEnter();
    }
}
