package com.setayesh.planit.ui;

import com.setayesh.planit.core.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input/output for PlanIt v2.
 */
public class ConsoleUI {
    private final TaskService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(TaskService service) {
        this.service = service;
    }

    public void start() {
        UIHelper.printHeader(UIHelper.t("welcome"));

        boolean running = true;
        while (running) {
            System.out.println();
            UIHelper.printPageHeader("home");
            TodoPrinter.printTodoList((java.util.ArrayList<Task>) service.getAll());

            System.out.println("""
                    1 - Add task
                    2 - Show all tasks
                    3 - Mark task done/undone
                    4 - Exit
                    """);

            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addTask();
                case "2" -> showTasks();
                case "3" -> toggleDone();
                case "4" -> running = false;
                default -> System.out.println(UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
            }
        }

        System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("goodbye") + UIHelper.RESET);
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
        Priority priority = Priority.MEDIUM;
        if (!prioStr.isEmpty()) {
            switch (prioStr) {
                case "1" -> priority = Priority.HIGH;
                case "2" -> priority = Priority.MEDIUM;
                case "3" -> priority = Priority.LOW;
                default -> System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("invalid_priority") + UIHelper.RESET);
            }
        }

        service.add(new Task(title, deadline, priority));
        System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("task_added") + UIHelper.RESET);
    }

    private void showTasks() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_tasks") + UIHelper.RESET);
            return;
        }
        TodoPrinter.printTodoList((java.util.ArrayList<Task>) tasks);
    }

    private void toggleDone() {
        List<Task> tasks = service.getAll();
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_to_mark") + UIHelper.RESET);
            return;
        }

        showTasks();
        System.out.print(UIHelper.t("enter_num_mark"));
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < tasks.size()) {
                Task t = tasks.get(index);
                if (t.isDone())
                    t.markUndone();
                else
                    t.markDone();
                service.add(t); // overwrites and persists
                System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("marked_done") + UIHelper.RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
        }
    }
}
