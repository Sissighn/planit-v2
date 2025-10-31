package com.setayesh.planit.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.setayesh.planit.core.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles reading and writing Task data to a local JSON file safely.
 */
public class JsonTaskRepository {
    private final File tasksFile;
    private final File archiveFile;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public JsonTaskRepository(String basePath) {
        this.tasksFile = new File(basePath, "planit_tasks.json");
        this.archiveFile = new File(basePath, "planit_archive.json");

        // Ensure directory exists
        File dir = new File(basePath);
        if (!dir.exists())
            dir.mkdirs();
    }

    public List<Task> load() {
        return readList(tasksFile);
    }

    public void save(List<Task> tasks) {
        writeList(tasksFile, tasks);
    }

    public List<Task> loadArchive() {
        return readList(archiveFile);
    }

    public void saveArchive(List<Task> archive) {
        writeList(archiveFile, archive);
    }

    private List<Task> readList(File file) {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Task>>() {
            });
        } catch (IOException e) {
            System.err.println("⚠️ Error reading " + file.getName() + ": " + e.getMessage());

            try {
                File backup = new File(file.getParent(), file.getName() + ".corrupted");
                Files.copy(file.toPath(), backup.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.err.println("🩹 Backup saved as " + backup.getName() + " and creating new empty file.");
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<Task>());
            } catch (IOException ex) {
                System.err.println("⚠️ Failed to create backup: " + ex.getMessage());
            }

            return new ArrayList<>();
        }
    }

    private void writeList(File file, List<Task> tasks) {
        File temp = new File(file.getAbsolutePath() + ".tmp");

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(temp, tasks);

            Files.move(temp.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.err.println("⚠️ Error saving " + file.getName() + ": " + e.getMessage());
            temp.delete(); // temp-Datei aufräumen, falls etwas schiefging
        }
    }
}
