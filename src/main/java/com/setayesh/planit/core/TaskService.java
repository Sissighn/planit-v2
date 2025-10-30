package com.setayesh.planit.core;

import com.setayesh.planit.storage.JsonTaskRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class that manages the logic for adding, editing, deleting,
 * sorting, searching, and saving tasks.
 * Works with the JsonTaskRepository for persistent storage.
 */
public class TaskService {
    private final JsonTaskRepository repo;
    private final List<Task> tasks;

    public TaskService(JsonTaskRepository repo) {
        this.repo = repo;
        this.tasks = new ArrayList<>(repo.load());
    }

    // BASIC CRUD
    public List<Task> getAll() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        save();
    }

    public void deleteTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("⚠️ Invalid index.");
            return;
        }
        tasks.remove(index);
        save();
    }

    public void markDone(int index) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("⚠️ Invalid index.");
            return;
        }
        Task task = tasks.get(index);
        task.markDone();
        save();
    }

    public void markUndone(int index) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("⚠️ Invalid index.");
            return;
        }
        Task task = tasks.get(index);
        task.markUndone();
        save();
    }

    public void editTask(int index, String newTitle, LocalDate newDeadline, Priority newPriority) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("⚠️ Invalid index.");
            return;
        }
        Task task = tasks.get(index);
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            task.setTitle(newTitle.trim());
        }
        if (newDeadline != null) {
            task.setDeadline(newDeadline);
        }
        if (newPriority != null) {
            task.setPriority(newPriority);
        }
        save();
    }

    // SORTING
    public void sortByDeadline() {
        tasks.sort((a, b) -> {
            if (a.getDeadline() == null && b.getDeadline() == null)
                return 0;
            if (a.getDeadline() == null)
                return 1;
            if (b.getDeadline() == null)
                return -1;
            return a.getDeadline().compareTo(b.getDeadline());
        });
    }

    public void sortByPriority() {
        tasks.sort((a, b) -> {
            if (a.getPriority() == null && b.getPriority() == null)
                return 0;
            if (a.getPriority() == null)
                return 1;
            if (b.getPriority() == null)
                return -1;
            return a.getPriority().compareTo(b.getPriority());
        });

        save();
    }

    public void sortByTitle() {
        tasks.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        save();
    }

    // SEARCH
    public List<Task> searchTasks(String keyword) {
        if (keyword == null || keyword.isBlank())
            return Collections.emptyList();
        String lower = keyword.toLowerCase();
        return tasks.stream()
                .filter(t -> t.getTitle() != null && t.getTitle().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // Archive
    public void archiveTask(int index) {
        if (index < 0 || index >= tasks.size())
            return;
        Task t = tasks.get(index);
        t.setArchived(true);

        List<Task> archived = new ArrayList<>(repo.loadArchive());
        archived.add(t);

        tasks.remove(index);
        repo.saveArchive(archived);
        save();
    }

    public List<Task> loadArchive() {
        return repo.loadArchive();
    }

    public void clearCompletedNotArchived() {
        tasks.removeIf(t -> t.isDone() && !t.isArchived());
        save();
    }

    // STORAGE
    public void save() {
        repo.save(tasks);
    }

    // UTIL
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public int size() {
        return tasks.size();
    }

    public Task get(int index) {
        if (index < 0 || index >= tasks.size())
            return null;
        return tasks.get(index);
    }
}
