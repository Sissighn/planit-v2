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

    // Constructor for CLI and tests (no instanceRepo needed)
    public TaskService(TaskRepository repo) {
        this.repo = Objects.requireNonNull(repo);
        this.instanceRepo = new TaskInstanceRepository(); // IN-MEMORY fallback
        this.tasks = new ArrayList<>(repo.findAll());
    }

    // ---------------------------------------------------------
    // BASIC CRUD
    // ---------------------------------------------------------

    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    public Optional<Task> findById(UUID id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public void addTask(Task t) {
        tasks.add(t);
        save();
    }

    public void editTask(UUID id, String newTitle, LocalDate newDeadline, Priority newPriority) {
        Task task = findById(id).orElseThrow();

        // title
        if (newTitle != null && !newTitle.isBlank()) {
            task.setTitle(newTitle.trim());
        }

        // deadline
        if (newDeadline != null) {
            task.setDeadline(newDeadline);
        }

        // priority
        if (newPriority != null) {
            task.setPriority(newPriority);
        }

        // After editing, recalculate next occurrence for recurring tasks
        if (task.getRepeatFrequency() != RepeatFrequency.NONE) {
            List<LocalDate> completed = instanceRepo.findCompletedDates(id);
            LocalDate next = RecurrenceUtils.computeNextOccurrence(task, completed);
            task.setNextOccurrence(next);
        }

        save();
    }

    public void deleteTask(UUID id) {
        tasks.removeIf(t -> t.getId().equals(id));
        instanceRepo.deleteForTask(id); // remove instance history
        save();
    }

    // ---------------------------------------------------------
    // ARCHIVE
    // ---------------------------------------------------------

    public void archiveTask(UUID id) {
        Task t = findById(id).orElseThrow();
        t.setArchived(true);

        // move to archive storage
        List<Task> archived = new ArrayList<>(repo.loadArchive());
        archived.add(t);

        tasks.remove(t);
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

    // ---------------------------------------------------------
    // ONE-TIME TASK COMPLETION
    // ---------------------------------------------------------

    public void markDone(UUID id) {
        Task t = findById(id).orElseThrow();
        t.markDone();
        save();
    }

    public void markUndone(UUID id) {
        Task t = findById(id).orElseThrow();
        t.markUndone();
        save();
    }

    // ---------------------------------------------------------
    // RECURRING INSTANCE SYSTEM (APPLE REMINDERS STYLE)
    // ---------------------------------------------------------

    /**
     * Mark *one specific instance* of a recurring task as completed.
     */
    public void markInstanceCompleted(UUID taskId, LocalDate date) {
        if (!instanceRepo.exists(taskId, date)) {
            instanceRepo.markCompleted(taskId, date);
        }

        Task t = findById(taskId).orElseThrow();

        List<LocalDate> completed = instanceRepo.findCompletedDates(taskId);
        LocalDate next = RecurrenceUtils.computeNextOccurrence(t, completed);

        t.setNextOccurrence(next);
        save();
    }

    /**
     * Remove exactly one occurrence from the recurrence series.
     */
    public void excludeDate(UUID taskId, LocalDate date) {
        Task t = findById(taskId).orElseThrow();
        t.addExcludedDate(date);

        List<LocalDate> completed = instanceRepo.findCompletedDates(taskId);
        LocalDate next = RecurrenceUtils.computeNextOccurrence(t, completed);

        t.setNextOccurrence(next);
        save();
    }

    /**
     * Stop generating future occurrences after a given date.
     */
    public void deleteFutureOccurrences(UUID id, LocalDate fromDate) {
        Task t = findById(id).orElseThrow();
        t.setRepeatUntil(fromDate.minusDays(1));

        List<LocalDate> completed = instanceRepo.findCompletedDates(id);
        LocalDate next = RecurrenceUtils.computeNextOccurrence(t, completed);

        t.setNextOccurrence(next);
        save();
    }

    public boolean isInstanceCompleted(UUID id, LocalDate date) {
        return instanceRepo.exists(id, date);
    }

    // ---------------------------------------------------------
    // RECURRING LOGIC (READ)
    // ---------------------------------------------------------

    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> result = new ArrayList<>();

        for (Task t : tasks) {
            if (t.isArchived())
                continue;

            // recurring logic: delegated to Task.occursOn()
            if (t.occursOn(date)) {
                result.add(t);
            }
        }

        return result;
    }

    // ---------------------------------------------------------
    // SORTING
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // STORAGE
    // ---------------------------------------------------------

    public void save() {
        repo.saveAll(tasks);
    }
}
