package com.setayesh.planit.settings;

import java.io.IOException;
import java.nio.file.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles persistent storage of user settings.
 * Uses JSON with Jackson and atomic writes for safety.
 */
public class SettingsManager {

    private static final Path SETTINGS_PATH = Path.of(System.getProperty("user.home"), "planit_settings.json");

    // ================== LOAD ================== //
    public static AppSettings load() {
        if (!Files.exists(SETTINGS_PATH)) {
            return new AppSettings(); // returns default EN + COUNTS
        }

        try {
            ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
            AppSettings loaded = mapper.readValue(
                    Files.newBufferedReader(SETTINGS_PATH),
                    AppSettings.class);

            if (loaded.getLanguage() == null)
                loaded.setLanguage(com.setayesh.planit.ui.UIHelper.Language.EN);
            if (loaded.getDashboardMode() == null)
                loaded.setDashboardMode(com.setayesh.planit.ui.UIHelper.DashboardMode.COUNTS);

            return loaded;

        } catch (IOException e) {
            System.err.println("⚠️ Could not load settings: " + e.getMessage());
            return new AppSettings();
        }
    }

    // ================== SAVE ================== //
    public static void save(AppSettings settings) {
        Path tmp = SETTINGS_PATH.resolveSibling(SETTINGS_PATH.getFileName() + ".tmp");

        try {
            ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

            try (var writer = Files.newBufferedWriter(tmp)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(writer, settings);
            }

            Files.move(
                    tmp,
                    SETTINGS_PATH,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);

        } catch (IOException e) {
            System.err.println("⚠️ Could not save settings: " + e.getMessage());
            try {
                Files.deleteIfExists(tmp);
            } catch (IOException ignored) {
            }
        }
    }
}
