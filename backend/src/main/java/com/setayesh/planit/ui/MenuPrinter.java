package com.setayesh.planit.ui;

public class MenuPrinter {

    private static final int DEFAULT_WIDTH = 60;

    public static void printMainMenu(Theme theme) {
        String[] buttons = {
                "1 - Add",
                "2 - Edit",
                "3 - Done/Undone",
                "4 - Delete",
                "5 - Archive",
                "6 - View archive",
                "7 - Clear completed",
                "8 - Sorting",
                "9 - Settings",
                "10 - Exit"
        };

        int buttonWidth = 0;
        for (String b : buttons)
            if (b.length() > buttonWidth)
                buttonWidth = b.length();
        buttonWidth += 4;

        int consoleWidth = getConsoleWidth();
        if (consoleWidth <= 0)
            consoleWidth = DEFAULT_WIDTH;

        int buttonsPerLine = Math.max(1, consoleWidth / (buttonWidth + 2));

        for (int i = 0; i < buttons.length; i += buttonsPerLine) {
            int end = Math.min(i + buttonsPerLine, buttons.length);

            // top border
            for (int j = i; j < end; j++)
                System.out.print(theme.line() + "╔" + "═".repeat(buttonWidth) + "╗ " + Colors.RESET);
            System.out.println();

            // text
            for (int j = i; j < end; j++) {
                String b = buttons[j];
                System.out.print(
                        theme.line() + "║ " + b + " ".repeat(buttonWidth - b.length() - 1) + "║ " + Colors.RESET);
            }
            System.out.println();

            // bottom border
            for (int j = i; j < end; j++)
                System.out.print(theme.line() + "╚" + "═".repeat(buttonWidth) + "╝ " + Colors.RESET);
            System.out.println("\n");
        }
    }

    private static int getConsoleWidth() {
        try {
            String columns = System.getenv("COLUMNS");
            if (columns != null)
                return Integer.parseInt(columns);
        } catch (NumberFormatException ignored) {
        }
        return 0;
    }
}
