package com.setayesh.planit.ui;

import com.setayesh.planit.core.Priority;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonEmpty(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty())
                System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("please_non_empty") + Colors.RESET);
        } while (input.isEmpty());
        return input;
    }

    public LocalDate readDate(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty())
            return null;

        try {
            return LocalDate.parse(input, FMT);
        } catch (Exception e) {
            System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("invalid_date") + Colors.RESET);
            return null;
        }
    }

    public Priority readPriority(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty())
            return null;

        return switch (input) {
            case "1" -> Priority.HIGH;
            case "2" -> Priority.MEDIUM;
            case "3" -> Priority.LOW;
            default -> {
                System.out.println(Colors.PASTEL_YELLOW + UIHelper.t("invalid_priority") + Colors.RESET);
                yield null;
            }
        };
    }

    public int readIndex(String prompt, int size, boolean allowZeroCancel) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int num = Integer.parseInt(input);
                if (allowZeroCancel && num == 0)
                    return -1;
                int index = num - 1;
                if (index >= 0 && index < size)
                    return index;
            } catch (NumberFormatException ignored) {
            }
            System.out.println(Colors.PASTEL_RED + UIHelper.t("please_number") + Colors.RESET);
        }
    }

    public void waitForEnter() {
        System.out.println(Colors.PASTEL_PURPLE + UIHelper.t("press_enter") + Colors.RESET);
        scanner.nextLine();
    }
}
