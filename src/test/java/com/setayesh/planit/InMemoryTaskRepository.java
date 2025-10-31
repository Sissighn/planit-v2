package com.setayesh.planit;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.storage.JsonTaskRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake in-memory repository for testing TaskService without touching files.
 */
public class InMemoryTaskRepository extends JsonTaskRepository {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Task> archived = new ArrayList<>();

    public InMemoryTaskRepository() {
        super("dummy-path"); // Call parent constructor if needed
    }

    @Override
    public List<Task> load() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void save(List<Task> all) {
        tasks.clear();
        tasks.addAll(all);
    }

    @Override
    public List<Task> loadArchive() {
        return new ArrayList<>(archived);
    }

    @Override
    public void saveArchive(List<Task> all) {
        archived.clear();
        archived.addAll(all);
    }
}
