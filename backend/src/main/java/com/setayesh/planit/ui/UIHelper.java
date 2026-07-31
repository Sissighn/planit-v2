package com.setayesh.planit.ui;

import com.setayesh.planit.i18n.Translations;
import com.setayesh.planit.settings.AppSettings;
import com.setayesh.planit.settings.SettingsManager;
import com.setayesh.planit.settings.AppContext;

public class UIHelper {

    public enum Language {
        EN,
        DE,
    }

    public enum DashboardMode {
        COUNTS, PERCENTAGES, BOTH
    }

    // -------------------- SETTINGS -------------------- //
    public static void loadSettings() {
        AppSettings loaded = SettingsManager.load();
        if (loaded.getLanguage() != null)
            AppContext.setLanguage(loaded.getLanguage());
        if (loaded.getDashboardMode() != null)
            AppContext.setDashboardMode(loaded.getDashboardMode());
    }

    // -------------------- LANGUAGE -------------------- //
    public static void setLanguage(Language lang) {
        AppContext.setLanguage(lang);
        SettingsManager.save(new AppSettings(
                AppContext.getLanguage(),
                AppContext.getDashboardMode()));
    }

    public static Language getLanguage() {
        return AppContext.getLanguage();
    }

    public static String t(String key) {
        return Translations.get(key, AppContext.getLanguage());
    }

    // -------------------- DASHBOARD -------------------- //
    public static DashboardMode getDashboardMode() {
        return AppContext.getDashboardMode();
    }

    public static void setDashboardMode(DashboardMode mode) {
        AppContext.setDashboardMode(mode);
        SettingsManager.save(new AppSettings(
                AppContext.getLanguage(),
                AppContext.getDashboardMode()));
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

    public static void printDashboard(int archived, int completed, int total) {
        Language language = AppContext.getLanguage();
        DashboardMode dashboardMode = AppContext.getDashboardMode();

        String archivedLabel = (language == Language.EN) ? "Archived" : "Archiviert";
        String completedLabel = (language == Language.EN) ? "Completed" : "Erledigt";
        String totalLabel = (language == Language.EN) ? "Total" : "Gesamt";

        if (total <= 0)
            total = 1;

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
                    "📦 %s: %.0f%%  |  ✅ %s: %.0f%%  |  📋 %s: %d (100%%)",
                    archivedLabel, archivedPercent,
                    completedLabel, completedPercent,
                    totalLabel, total);

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
