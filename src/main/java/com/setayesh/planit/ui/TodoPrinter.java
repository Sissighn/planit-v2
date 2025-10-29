package com.setayesh.planit.ui;

import java.util.ArrayList;
import com.setayesh.planit.core.Task;
import com.setayesh.planit.util.AnsiUtils;

public class TodoPrinter {

    private static final int NUM_WIDTH = 3;
    private static final int TASK_WIDTH = 40;

    public static void printTodoList(ArrayList<Task> tasks) {
        String placeHolder = UIHelper.PASTEL_PINK;
        String lineColor = UIHelper.PASTEL_BROWN;
        String checkColor = UIHelper.PASTEL_GREEN;
        String reset = UIHelper.RESET;

        String topLine = lineColor + "╔" + repeat("═", NUM_WIDTH) + "╦" + repeat("═", TASK_WIDTH) + "╗" + reset;
        String midLine = lineColor + "╠" + repeat("═", NUM_WIDTH) + "╬" + repeat("═", TASK_WIDTH) + "╣" + reset;
        String lowLine = lineColor + "╚" + repeat("═", NUM_WIDTH) + "╩" + repeat("═", TASK_WIDTH) + "╝" + reset;

        System.out.println(topLine);
        System.out.printf(lineColor + "║%-" + NUM_WIDTH + "s║%-" + TASK_WIDTH + "s║%n" + reset, "", "Task");
        System.out.println(midLine);

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            String statusSymbol = t.isDone() ? "✔" : " ";
            String coloredStatus = (t.isDone() ? checkColor : placeHolder) + "[" + statusSymbol + "]" + UIHelper.RESET;

            String priorityColor;
            String priorityText;
            if (t.getPriority() == null) {
                priorityColor = UIHelper.PASTEL_BROWN;
                priorityText = "—";
            } else {
                switch (t.getPriority()) {
                    case HIGH -> {
                        priorityColor = UIHelper.PASTEL_RED_URGENT;
                        priorityText = "HIGH";
                    }
                    case MEDIUM -> {
                        priorityColor = UIHelper.PASTEL_YELLOW;
                        priorityText = "MEDIUM";
                    }
                    case LOW -> {
                        priorityColor = UIHelper.PASTEL_CYAN;
                        priorityText = "LOW";
                    }
                    default -> {
                        priorityColor = UIHelper.PASTEL_BROWN;
                        priorityText = "—";
                    }
                }
            }

            String deadlineStr = (t.getDeadline() != null)
                    ? t.getDeadline().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    : "—";

            String left = coloredStatus + " ";
            String right = " (" + priorityColor + priorityText + reset + ")  " + deadlineStr;
            String title = t.getTitle();

            int spaceForTitle = Math.max(0,
                    TASK_WIDTH - AnsiUtils.visibleLength(left) - AnsiUtils.visibleLength(right));

            String titleShown = title;
            if (AnsiUtils.visibleLength(titleShown) > spaceForTitle) {
                int target = Math.max(0, spaceForTitle - 3);
                titleShown = AnsiUtils.clipVisible(titleShown, target) + (spaceForTitle >= 3 ? "..." : "");
            }

            String cell = left + titleShown + right;
            if (AnsiUtils.visibleLength(cell) > TASK_WIDTH)
                cell = AnsiUtils.clipVisible(cell, TASK_WIDTH);
            cell = AnsiUtils.padRight(cell, TASK_WIDTH);

            System.out.printf(lineColor + "║%-" + NUM_WIDTH + "d║" + reset + "%s" + lineColor + "║%n" + reset,
                    i + 1, cell);
        }

        System.out.println(lowLine);
    }

    private static String repeat(String s, int times) {
        return s.repeat(times);
    }
}
