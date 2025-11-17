package com.setayesh.planit.core;

import com.setayesh.planit.storage.TaskInstanceRepository;
import com.setayesh.planit.storage.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

            // Non-repeating: show only if deadline matches (or no deadline → ignore)
            if (t.getRepeatFrequency() == null || t.getRepeatFrequency() == RepeatFrequency.NONE) {
                if (t.getDeadline() != null && t.getDeadline().equals(date)) {
                    result.add(t);
                }
                continue;
            }

            // Repeating: use recurrence logic
            if (t.occursOn(date)) {
                result.add(t);
            }
        }

        return result;
    }

    /**
     * True if this task "occurs" on the given date, based on its recurrence
     * settings.
     * Non-recurring tasks: occur on their deadline date only (if set),
     * or always visible (if deadline is null).
     */
    public boolean occursOnDate(Task task, LocalDate date) {
        RepeatFrequency freq = task.getRepeatFrequency();
        if (freq == null || freq == RepeatFrequency.NONE) {
            // Non-recurring task: simple behavior
            if (task.getDeadline() == null) {
                return true;
            }
            return task.getDeadline().equals(date);
        }

        LocalDate start = (task.getDeadline() != null)
                ? task.getDeadline()
                : task.getCreatedAt().toLocalDate();

        if (date.isBefore(start)) {
            return false;
        }

        if (task.getRepeatUntil() != null && date.isAfter(task.getRepeatUntil())) {
            return false;
        }

        Integer interval = (task.getRepeatInterval() != null && task.getRepeatInterval() > 0)
                ? task.getRepeatInterval()
                : 1;

        return switch (freq) {
            case DAILY -> matchesDaily(start, date, interval);
            case WEEKLY -> matchesWeekly(task, start, date, interval);
            case MONTHLY -> matchesMonthly(start, date, interval);
            case YEARLY -> matchesYearly(start, date, interval);
            default -> false;
        };
    }

    private boolean matchesDaily(LocalDate start, LocalDate date, int interval) {
        long days = ChronoUnit.DAYS.between(start, date);
        return days >= 0 && days % interval == 0;
    }

    private boolean matchesWeekly(Task task, LocalDate start, LocalDate date, int interval) {
        long days = ChronoUnit.DAYS.between(start, date);
        if (days < 0)
            return false;

        long weeks = days / 7;
        if (weeks % interval != 0)
            return false;

        // Check day-of-week filter if present
        String repeatDays = task.getRepeatDays();
        if (repeatDays == null || repeatDays.isBlank()) {
            return true;
        }

        DayOfWeek dow = date.getDayOfWeek(); // MONDAY...SUNDAY
        String token = mapDayOfWeekToShort(dow); // "MON", "TUE", ...

        String[] parts = repeatDays.split(",");
        for (String p : parts) {
            if (token.equalsIgnoreCase(p.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesMonthly(LocalDate start, LocalDate date, int interval) {
        if (date.getDayOfMonth() != start.getDayOfMonth()) {
            return false;
        }
        long months = ChronoUnit.MONTHS.between(
                start.withDayOfMonth(1),
                date.withDayOfMonth(1));
        return months >= 0 && months % interval == 0;
    }

    private boolean matchesYearly(LocalDate start, LocalDate date, int interval) {
        if (date.getMonth() != start.getMonth() || date.getDayOfMonth() != start.getDayOfMonth()) {
            return false;
        }
        long years = ChronoUnit.YEARS.between(start, date);
        return years >= 0 && years % interval == 0;
    }

    private String mapDayOfWeekToShort(DayOfWeek dow) {
        // Maps MONDAY -> "MON", etc.
        return switch (dow) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
            default -> "";
        };
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
}
