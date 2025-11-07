package com.setayesh.planit.core;

import com.setayesh.planit.storage.TaskRepository;

import java.time.LocalDate;
import java.util.*;

/**
 * Service class that manages the logic for adding, editing, deleting,
 * sorting, searching, and saving tasks.
 * Works with the JsonTaskRepository for persistent storage.
 */
public class TaskService {
    private final TaskRepository repo;
    private final List<Task> tasks;

    public TaskService(TaskRepository repo) {
        this.repo = Objects.requireNonNull(repo);
        this.tasks = new ArrayList<>(repo.findAll());
    }

    // BASIC CRUD
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    public void addTask(Task task) {
        tasks.add(task);
        save();
    }

    public void deleteTask(UUID id) {
        tasks.removeIf(t -> t.getId().equals(id));
        save();
    }

    public void markDone(UUID id) {
        tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .ifPresent(t -> {
                    t.markDone();
                    save();
                });
    }

    public void markUndone(UUID id) {
        tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .ifPresent(t -> {
                    t.markUndone();
                    save();
                });
    }

    public void editTask(UUID id, String newTitle, LocalDate newDeadline, Priority newPriority) {
        tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .ifPresent(task -> {
                    if (newTitle != null && !newTitle.trim().isEmpty())
                        task.setTitle(newTitle.trim());
                    if (newDeadline != null)
                        task.setDeadline(newDeadline);
                    if (newPriority != null)
                        task.setPriority(newPriority);
                    save();
                });
    }

    public Optional<Task> findById(UUID id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    // SORTING
    public void sortByDeadline() {
        tasks.sort(Comparator.comparing(Task::getDeadline,
                Comparator.nullsLast(Comparator.naturalOrder())));
        save();
    }

    public void sortByPriority() {
        tasks.sort(Comparator.comparing(Task::getPriority,
                Comparator.nullsLast(Comparator.naturalOrder())));
        save();
    }

    public void sortByTitle() {
        tasks.sort(Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER));
        save();
    }

    // Archive
    public void archiveTask(UUID id) {
        findById(id).ifPresent(t -> {
            t.setArchived(true);
            List<Task> archived = new ArrayList<>(repo.loadArchive());
            archived.add(t);
            tasks.removeIf(task -> task.getId().equals(id));
            repo.saveArchive(archived);
            save();
        });
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
        repo.saveAll(tasks);
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
