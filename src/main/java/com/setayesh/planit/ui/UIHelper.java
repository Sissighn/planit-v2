package com.setayesh.planit.ui;

import java.io.*;
import com.setayesh.planit.i18n.Translations;

public class UIHelper {

    public enum Language {
        EN,
        DE,
    }

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";

    public static final String PASTEL_PINK = "\u001B[38;5;175m";
    public static final String PASTEL_PURPLE = "\u001B[38;5;183m";
    public static final String PASTEL_SALMON_PINK = "\u001B[38;5;205m";
    public static final String PASTEL_YELLOW = "\u001B[38;5;229m";
    public static final String PASTEL_CYAN = "\u001B[38;5;159m";
    public static final String PASTEL_BROWN = "\u001B[38;5;180m";
    public static final String PASTEL_RED = "\u001B[38;5;131m";
    public static final String PASTEL_RED_URGENT = "\u001b[38;2;210;58;58m";
    public static final String PASTEL_GREEN = "\u001B[38;5;120m";

    private static final String SETTINGS_FILE = "settings.cfg";
    private static Language language = Language.EN;

    public static void setLanguage(Language lang) {
        language = lang;
    }

    public static Language getLanguage() {
        return language;
    }

    public static String t(String key) {
        return Translations.get(key, language);
    }

    public static void printPageHeader(String sectionKey) {
        clearScreen();

        String title = switch (sectionKey) {
            case "home" -> "Home";
            case "edit" -> "Edit Task";
            case "archive" -> "Archive";
            case "viewArchive" -> "View Archive";
            case "clear" -> "Clear Completed";
            case "settings" -> "Settings";
            case "sort" -> "Sort";
            case "add" -> "Add Task";
            case "delete" -> "Delete";
            default -> "To-Do List";
        };

        String line = "══════════════════════════════════════════════";
        System.out.println(PASTEL_PURPLE + line + RESET);
        System.out.println(BOLD + PASTEL_PINK + "  " + title + RESET);
        System.out.println(PASTEL_PURPLE + line + RESET);
    }

    public static void printHeader(String title) {
        clearScreen();
        String line = "══════════════════════════════════════════════";
        System.out.println(PASTEL_PURPLE + line + RESET);
        System.out.println(BOLD + PASTEL_PINK + "  " + title + RESET);
        System.out.println(PASTEL_PURPLE + line + RESET);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static Language loadLanguageFromFile() {
        File f = new File(SETTINGS_FILE);
        if (!f.exists())
            return Language.EN;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("lang=")) {
                    String v = line.substring(5).trim().toUpperCase();
                    if (v.equals("DE"))
                        return Language.DE;
                    return Language.EN;
                }
            }
        } catch (IOException ignored) {
        }
        return Language.EN;
    }

    public static void saveLanguageToFile(Language lang) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
            pw.println("lang=" + (lang == Language.DE ? "DE" : "EN"));
        } catch (IOException e) {
            System.out.println(PASTEL_RED_URGENT + "Could not save settings: " + e.getMessage() + RESET);
        }
    }

    public static void printDashboard(int archived, int completed, int total) {
        String archivedLabel = (language == Language.EN) ? "Archived" : "Archiviert";
        String completedLabel = (language == Language.EN) ? "Completed" : "Erledigt";
        String totalLabel = (language == Language.EN) ? "Total" : "Gesamt";

        String line = "📦 " + archivedLabel + ": " + archived +
                " | ✅ " + completedLabel + ": " + completed +
                " | 📋 " + totalLabel + ": " + total;
        System.out.println(PASTEL_CYAN + line + RESET);
    }
}
