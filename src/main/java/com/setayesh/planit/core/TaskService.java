package com.setayesh.planit.core;

import com.setayesh.planit.storage.TaskInstanceRepository;
import com.setayesh.planit.storage.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TaskService {
    private final TaskRepository repo;
    private final TaskInstanceRepository instanceRepo;
    private final List<Task> tasks;

    @Autowired
    public TaskService(TaskRepository repo, TaskInstanceRepository instanceRepo) {
        this.repo = Objects.requireNonNull(repo);
        this.instanceRepo = instanceRepo;
        this.tasks = new ArrayList<>(repo.findAll());
    }

    // Secondary constructor for tests (no instanceRepo needed).
    public TaskService(TaskRepository repo) {
        this(repo, null);
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
        if (instanceRepo != null) {
            instanceRepo.deleteForTask(id);
        }
        save();
    }

    public void markDone(UUID id) {
        tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .ifPresent(t -> {

                    // ✅ RECURRING TASKS (DAILY/WEEKLY/MONTHLY/YEARLY)
                    if (t.getRepeatFrequency() != null && t.getRepeatFrequency() != RepeatFrequency.NONE) {

                        // 1) Welche Instanz wurde erledigt?
                        // → normalerweise: aktuelle Deadline = fälliges Datum
                        LocalDate instanceDate = t.getDeadline();
                        if (instanceDate == null) {
                            // Fallback, falls Deadline mal nicht gesetzt ist
                            instanceDate = LocalDate.now();
                        }

                        // 2) Diese Instanz als "completed" speichern
                        markInstanceCompleted(t.getId(), instanceDate);

                        // 3) Nächste Fälligkeit berechnen (Apple-Style: Deadline springt weiter)
                        LocalDate next = t.computeNextOccurrence();
                        if (next != null) {
                            t.setDeadline(next);
                        }

                        // 4) Serie bleibt als UNDONE (damit im Dashboard normal angezeigt wird)
                        t.markUndone();
                    }

                    // ✅ ONE-TIME TASKS
                    else {
                        t.markDone();
                    }

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

    // ============================================================
    // Recurrence logic (backend-only for now – used by future API)
    // ============================================================

    /**
     * Returns all tasks that are relevant for the given date,
     * including repeating tasks that occur on that date.
     */
    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> all = getAll();
        List<Task> result = new ArrayList<>();

        for (Task t : all) {
            if (t.isArchived()) {
                continue;
            }

            // Repeating: use recurrence logic
            if (t.occursOn(date)) {
                result.add(t);
            }
        }

        return result;
    }

    // Optional: use task_instances_completed later for UI like
    // "this specific occurrence is already done today".
    public void markOccurrenceDone(UUID taskId, LocalDate date) {
        if (instanceRepo != null) {
            instanceRepo.markCompleted(taskId, date);
        }
    }

    public boolean isOccurrenceCompleted(UUID taskId, LocalDate date) {
        if (instanceRepo == null) {
            return false;
        }
        return instanceRepo.isCompletedOnDate(taskId, date);
    }

    public void deleteFutureOccurrences(UUID id, LocalDate fromDate) {
        Task task = findById(id).orElseThrow();
        task.setRepeatUntil(fromDate.minusDays(1));
        save();
    }

    public void excludeDate(UUID id, LocalDate date) {
        Task task = findById(id).orElseThrow();
        task.addExcludedDate(date);
        save();
    }

    public void markInstanceCompleted(UUID taskId, LocalDate date) {
        if (!instanceRepo.exists(taskId, date)) {
            instanceRepo.markCompleted(taskId, date);
        }
    }

    public boolean isInstanceCompleted(UUID taskId, LocalDate date) {
        return instanceRepo.exists(taskId, date);
    }

}
