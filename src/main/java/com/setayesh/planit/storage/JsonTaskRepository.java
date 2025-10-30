package com.setayesh.planit.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.setayesh.planit.core.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles reading and writing Task data to a local JSON file.
 */
public class JsonTaskRepository {
    private final File tasksFile;
    private final File archiveFile;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonTaskRepository(String basePath) {
        this.tasksFile = new File(basePath, "planit_tasks.json");
        this.archiveFile = new File(basePath, "planit_archive.json");

        // Ensure directory exists
        File dir = new File(basePath);
        if (!dir.exists())
            dir.mkdirs();
    }

    // TASKS
    public List<Task> load() {
        return readList(tasksFile);
    }

    public void save(List<Task> tasks) {
        writeList(tasksFile, tasks);
    }

    // ARCHIVE
    public List<Task> loadArchive() {
        return readList(archiveFile);
    }

    public void saveArchive(List<Task> archive) {
        writeList(archiveFile, archive);
    }

    // Internal helpers
    private List<Task> readList(File file) {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Task>>() {
            });
        } catch (IOException e) {
            System.err.println("⚠️ Error reading " + file.getName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void writeList(File file, List<Task> tasks) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
        } catch (IOException e) {
            System.err.println("⚠️ Error saving " + file.getName() + ": " + e.getMessage());
        }
    }
}
