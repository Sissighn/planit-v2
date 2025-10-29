package com.setayesh.planit.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.setayesh.planit.core.Task;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles reading and writing Task data to a local JSON file.
 */
public class JsonTaskRepository {
    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonTaskRepository(String path) {
        this.file = new File(path);
    }

    public List<Task> load() {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Task>>() {});
        } catch (Exception e) {
            System.err.println("⚠️ Error reading tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void save(List<Task> tasks) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
        } catch (Exception e) {
            System.err.println("⚠️ Error saving tasks: " + e.getMessage());
        }
    }
}

