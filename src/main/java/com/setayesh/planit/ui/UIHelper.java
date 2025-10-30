package com.setayesh.planit.ui;

import java.io.*;
import com.setayesh.planit.i18n.Translations;
import com.setayesh.planit.settings.AppSettings;

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

    private static final String SETTINGS_PATH = System.getProperty("user.home") + "/planit_settings.json";

    public static void setLanguage(Language lang) {
        language = lang;
        saveSettings();
    }

    public static Language getLanguage() {
        return language;
    }

    public static String t(String key) {
        return Translations.get(key, language);
    }

    public static DashboardMode getDashboardMode() {
        return dashboardMode;
    }

    public static void setDashboardMode(DashboardMode mode) {
        dashboardMode = mode;
        saveDashboardModeToFile(mode);
        saveSettings();

    }

    public static void saveSettings() {
        java.nio.file.Path path = java.nio.file.Path.of(SETTINGS_PATH);
        java.nio.file.Path tmp = path.resolveSibling(path.getFileName() + ".tmp");

        AppSettings settings = new AppSettings();
        settings.setLanguage(language);
        settings.setDashboardMode(dashboardMode);

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.findAndRegisterModules();

            try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(tmp)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(writer, settings);
            }

            java.nio.file.Files.move(
                    tmp,
                    path,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);

        } catch (IOException e) {
            System.err.println("⚠️ Could not save settings: " + e.getMessage());
            try {
                java.nio.file.Files.deleteIfExists(tmp);
            } catch (IOException ignore) {
            }
        }
    }

    public static void loadSettings() {
        java.nio.file.Path path = java.nio.file.Path.of(SETTINGS_PATH);
        if (!java.nio.file.Files.exists(path)) {
            return;
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.findAndRegisterModules();

            AppSettings loaded = mapper.readValue(
                    java.nio.file.Files.newBufferedReader(path),
                    AppSettings.class);

            if (loaded.getLanguage() != null)
                language = loaded.getLanguage();

            if (loaded.getDashboardMode() != null)
                dashboardMode = loaded.getDashboardMode();

        } catch (IOException e) {
            System.err.println("⚠️ Could not load settings: " + e.getMessage());
        }
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

    public static void saveDashboardModeToFile(DashboardMode mode) {
        try {
            String path = System.getProperty("user.home") + "/planit_settings.json";
            java.nio.file.Files.writeString(
                    java.nio.file.Path.of(path),
                    "{\"dashboardMode\":\"" + mode.name() + "\"}");
        } catch (IOException e) {
            System.err.println("⚠️ Could not save dashboard setting: " + e.getMessage());
        }
    }

    public static void loadDashboardMode() {
        try {
            String path = System.getProperty("user.home") + "/planit_settings.json";
            if (!java.nio.file.Files.exists(java.nio.file.Path.of(path)))
                return;

            String json = java.nio.file.Files.readString(java.nio.file.Path.of(path));
            if (json.contains("PERCENTAGES"))
                dashboardMode = DashboardMode.PERCENTAGES;
            else if (json.contains("BOTH"))
                dashboardMode = DashboardMode.BOTH;
            else
                dashboardMode = DashboardMode.COUNTS;
        } catch (IOException e) {
            dashboardMode = DashboardMode.COUNTS;
        }
    }
}
