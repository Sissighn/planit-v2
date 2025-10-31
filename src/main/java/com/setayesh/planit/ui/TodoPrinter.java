package com.setayesh.planit.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.util.AnsiUtils;

public class TodoPrinter {

    private static final int NUM_WIDTH = 3;
    private static final int TASK_WIDTH = 40;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void printTodoList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println(Colors.PASTEL_YELLOW + "No tasks yet. Add one with option 1." + Colors.RESET);
            return;
        }

        Theme theme = Theme.defaultTheme();

        String topLine = buildLine("╔", "═", "╦", "╗", NUM_WIDTH, TASK_WIDTH);
        String midLine = buildLine("╠", "═", "╬", "╣", NUM_WIDTH, TASK_WIDTH);
        String lowLine = buildLine("╚", "═", "╩", "╝", NUM_WIDTH, TASK_WIDTH);

        System.out.println(topLine);
        System.out.printf(theme.line() + "║%-" + NUM_WIDTH + "s║%-" + TASK_WIDTH + "s║%n" + Colors.RESET, "", "Task");
        System.out.println(midLine);

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            printTaskRow(i + 1, t, TASK_WIDTH, theme);

        }

        System.out.println(lowLine);
        MenuPrinter.printMainMenu(theme);

    }

    private static void printTaskRow(int index, Task t, int taskWidth, Theme theme) {
        String statusSymbol = t.isDone() ? "✔" : " ";
        String coloredStatus = (t.isDone() ? theme.check() : theme.placeholder()) + statusSymbol + Colors.RESET;

        String priorityText = "—";
        String priorityColor = theme.line();

        if (t.getPriority() != null) {
            switch (t.getPriority()) {
                case HIGH -> {
                    priorityText = "HIGH";
                    priorityColor = Colors.PASTEL_RED_URGENT;
                }
                case MEDIUM -> {
                    priorityText = "MEDIUM";
                    priorityColor = Colors.PASTEL_YELLOW;
                }
                case LOW -> {
                    priorityText = "LOW";
                    priorityColor = Colors.PASTEL_CYAN;
                }
                default -> priorityText = "—";
            }
        }

        String deadlineStr = (t.getDeadline() != null)
                ? t.getDeadline().format(DATE_FMT)
                : "—";

        String title = (t.getTitle() != null) ? t.getTitle() : "(Untitled)";
        String left = coloredStatus + " ";
        String right = " (" + priorityColor + priorityText + Colors.RESET + ")  " + deadlineStr;

        int spaceForTitle = Math.max(0,
                taskWidth - AnsiUtils.visibleLength(left) - AnsiUtils.visibleLength(right));

        String titleShown = title;
        if (AnsiUtils.visibleLength(titleShown) > spaceForTitle) {
            int target = Math.max(0, spaceForTitle - 3);
            titleShown = AnsiUtils.clipVisible(titleShown, target) + (spaceForTitle >= 3 ? "..." : "");
        }

        String cell = left + titleShown + right;
        if (AnsiUtils.visibleLength(cell) > taskWidth)
            cell = AnsiUtils.clipVisible(cell, taskWidth);
        cell = AnsiUtils.padRight(cell, taskWidth);

        System.out.printf(
                theme.line() + "║%-" + NUM_WIDTH + "d║" + Colors.RESET + "%s" + theme.line() + "║%n" + Colors.RESET,
                index, cell);
    }

    private static String repeat(String s, int times) {
        return s.repeat(times);
    }

    private static String buildLine(String left, String fill, String middle, String right, int width1, int width2) {
        return Colors.PASTEL_BROWN + left + repeat(fill, width1) + middle + repeat(fill, width2) + right + Colors.RESET;
    }

}
