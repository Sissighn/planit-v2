package com.setayesh.planit.ui;

import java.io.*;
import com.setayesh.planit.i18n.Translations;
import com.setayesh.planit.settings.AppSettings;
import com.setayesh.planit.settings.SettingsManager;

public class UIHelper {

    public enum Language {
        EN,
        DE,
    }

    public enum DashboardMode {
        COUNTS, PERCENTAGES, BOTH
    }

    private static final String SETTINGS_FILE = "settings.cfg";
    private static Language language = Language.EN;
    private static DashboardMode dashboardMode = DashboardMode.COUNTS;

    public static void setLanguage(Language lang) {
        language = lang;
        SettingsManager.save(new AppSettings(language, dashboardMode));
    }

    // -------------------- LANGUAGE -------------------- //

    public static Language getLanguage() {
        return language;
    }

    public static String t(String key) {
        return Translations.get(key, language);
    }

    // -------------------- DASHBOARD -------------------- //
    public static DashboardMode getDashboardMode() {
        return dashboardMode;
    }

    public static void setDashboardMode(DashboardMode mode) {
        dashboardMode = mode;
        SettingsManager.save(new AppSettings(language, dashboardMode));
    }

    // -------------------- SETTINGS LOAD -------------------- //

    public static void loadSettings() {
        AppSettings loaded = SettingsManager.load();
        if (loaded.getLanguage() != null)
            language = loaded.getLanguage();
        if (loaded.getDashboardMode() != null)
            dashboardMode = loaded.getDashboardMode();
    }

    // -------------------- UI PRINTING -------------------- //
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
        System.out.println(Colors.PASTEL_PURPLE + line + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.PASTEL_PINK + "  " + title + Colors.RESET);
        System.out.println(Colors.PASTEL_PURPLE + line + Colors.RESET);
    }

    public static void printHeader(String title) {
        clearScreen();
        String line = "══════════════════════════════════════════════";
        System.out.println(Colors.PASTEL_PURPLE + line + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.PASTEL_PINK + "  " + title + Colors.RESET);
        System.out.println(Colors.PASTEL_PURPLE + line + Colors.RESET);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void saveLanguageToFile(Language lang) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
            pw.println("lang=" + (lang == Language.DE ? "DE" : "EN"));
        } catch (IOException e) {
            System.out.println(Colors.PASTEL_RED_URGENT + "Could not save settings: " + e.getMessage() + Colors.RESET);
        }
    }

    public static void printDashboard(int archived, int completed, int total) {
        String archivedLabel = (language == Language.EN) ? "Archived" : "Archiviert";
        String completedLabel = (language == Language.EN) ? "Completed" : "Erledigt";
        String totalLabel = (language == Language.EN) ? "Total" : "Gesamt";

        if (total <= 0)
            total = 1;

        // Prozentanteile
        double archivedPercent = (archived * 100.0 / total);
        double completedPercent = (completed * 100.0 / total);
        double totalPercent = 100.0;

        String line = switch (dashboardMode) {
            case COUNTS -> String.format(
                    "📦 %s: %d  |  ✅ %s: %d  |  📋 %s: %d",
                    archivedLabel, archived,
                    completedLabel, completed,
                    totalLabel, total);

            case PERCENTAGES -> String.format(
                    "📦 %s: %.0f%%  |  ✅ %s: %.0f%%  |  📋 %s: %.0f%%",
                    archivedLabel, archivedPercent,
                    completedLabel, completedPercent,
                    totalLabel, totalPercent);

            case BOTH -> String.format(
                    "📦 %s: %d (%.0f%%)  |  ✅ %s: %d (%.0f%%)  |  📋 %s: %d (%.0f%%)",
                    archivedLabel, archived, archivedPercent,
                    completedLabel, completed, completedPercent,
                    totalLabel, total, totalPercent);
        };

        System.out.println(Colors.PASTEL_CYAN + "──────────────────────────────────────────────" + Colors.RESET);
        System.out.println(Colors.PASTEL_CYAN + line + Colors.RESET);
        System.out.println(Colors.PASTEL_CYAN + "──────────────────────────────────────────────" + Colors.RESET);
    }
}
