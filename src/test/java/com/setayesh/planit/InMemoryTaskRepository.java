package com.setayesh.planit;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.storage.TaskRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake in-memory repository for testing TaskService without touching files.
 */
public class InMemoryTaskRepository implements TaskRepository {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Task> archived = new ArrayList<>();

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void saveAll(List<Task> all) {
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
        if (all != null)
            archived.addAll(all);
    }
}
