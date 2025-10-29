package com.setayesh.planit.core;

import com.setayesh.planit.storage.JsonTaskRepository;
import java.util.List;

public class TaskService {
    private final JsonTaskRepository repo;
    private final List<Task> tasks;

    public TaskService(JsonTaskRepository repo) {
        this.repo = repo;
        this.tasks = repo.load();
    }

    public void add(Task task) {
        tasks.add(task);
        repo.save(tasks);
    }

    public List<Task> getAll() {
        return tasks;
    }
}

