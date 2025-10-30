package com.setayesh.planit.ui;

import java.io.*;
import com.setayesh.planit.i18n.Translations;

public class UIHelper {

    public enum Language {
        EN,
        DE,
    }

    public enum DashboardMode {
        COUNTS, PERCENTAGES, BOTH
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
        try {
            java.util.Map<String, String> settings = new java.util.HashMap<>();
            settings.put("language", language.name());
            settings.put("dashboardMode", dashboardMode.name());

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new java.io.File(SETTINGS_PATH), settings);
        } catch (Exception e) {
            System.err.println("⚠️ Could not save settings: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadSettings() {
        try {
            java.io.File file = new java.io.File(SETTINGS_PATH);
            if (!file.exists())
                return;

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, String> settings = mapper.readValue(file, java.util.Map.class);

            // Sprache
            if (settings.containsKey("language")) {
                String lang = settings.get("language");
                if ("DE".equalsIgnoreCase(lang))
                    language = Language.DE;
                else
                    language = Language.EN;
            }

            // Dashboard-Modus
            if (settings.containsKey("dashboardMode")) {
                String mode = settings.get("dashboardMode");
                switch (mode.toUpperCase()) {
                    case "PERCENTAGES" -> dashboardMode = DashboardMode.PERCENTAGES;
                    case "BOTH" -> dashboardMode = DashboardMode.BOTH;
                    default -> dashboardMode = DashboardMode.COUNTS;
                }
            }

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

        System.out.println(PASTEL_CYAN + "──────────────────────────────────────────────" + RESET);
        System.out.println(PASTEL_CYAN + line + RESET);
        System.out.println(PASTEL_CYAN + "──────────────────────────────────────────────" + RESET);
    }

    public static void saveDashboardModeToFile(DashboardMode mode) {
        try {
            String path = System.getProperty("user.home") + "/planit_settings.json";
            java.nio.file.Files.writeString(
                    java.nio.file.Path.of(path),
                    "{\"dashboardMode\":\"" + mode.name() + "\"}");
        } catch (Exception e) {
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
        } catch (Exception e) {
            dashboardMode = DashboardMode.COUNTS;
        }
    }
}
