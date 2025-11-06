package com.setayesh.planit.storage;

import com.setayesh.planit.core.Task;
import java.util.*;

// In-memory repository for unit tests or demo runs.
public class InMemoryTaskRepository implements TaskRepository {
    private final List<Task> store = new ArrayList<>();
    private final List<Task> archive = new ArrayList<>();

    public InMemoryTaskRepository() {
    }

    public InMemoryTaskRepository(Collection<Task> seed) {
        if (seed != null)
            store.addAll(seed);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(store); // defensive copy
    }

    @Override
    public void saveAll(List<Task> tasks) {
        store.clear();
        if (tasks != null)
            store.addAll(tasks);
    }

    @Override
    public List<Task> loadArchive() {
        return new ArrayList<>(archive);
    }

    @Override
    public void saveArchive(List<Task> archive) {
        this.archive.clear();
        if (archive != null)
            this.archive.addAll(archive);
    }
}