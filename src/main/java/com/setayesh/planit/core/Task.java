package com.setayesh.planit.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Task {

    private final UUID id;
    private String title;
    private LocalDate deadline;
    private Priority priority;
    private Long groupId;
    private boolean done;
    private boolean archived;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String time;
    private String excludedDates;

    // 🔁 New: Repeat fields
    private RepeatFrequency repeatFrequency; // NONE, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    private String repeatDays; // For weekly: "MON,WED,FRI"
    private LocalDate repeatUntil; // End date
    private Integer repeatInterval; // For every X days/weeks/months

    // --- Constructors ---

    public Task() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.done = false;
        this.archived = false;
        this.repeatFrequency = RepeatFrequency.NONE;
    }

    public Task(String title) {
        this();
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        this.title = title.trim();
    }

    public Task(String title, LocalDate deadline, Priority priority) {
        this();
        setTitle(title);
        this.deadline = deadline;
        this.priority = priority;
    }

    // --- JSON Constructor (FULL) ---

    @JsonCreator
    public Task(
            @JsonProperty("id") UUID id,
            @JsonProperty("title") String title,
            @JsonProperty("deadline") LocalDate deadline,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("groupId") Long groupId,
            @JsonProperty("done") boolean done,
            @JsonProperty("archived") boolean archived,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt,
            @JsonProperty("repeatFrequency") RepeatFrequency repeatFrequency,
            @JsonProperty("repeatDays") String repeatDays,
            @JsonProperty("repeatUntil") LocalDate repeatUntil,
            @JsonProperty("excludedDates") String excludedDates,
            @JsonProperty("time") String time,
            @JsonProperty("repeatInterval") Integer repeatInterval) {

        this.id = (id != null) ? id : UUID.randomUUID();
        this.title = title;
        this.deadline = deadline;
        this.priority = priority;
        this.groupId = groupId;
        this.done = done;
        this.archived = archived;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.updatedAt = (updatedAt != null) ? updatedAt : LocalDateTime.now();
        this.repeatFrequency = (repeatFrequency != null) ? repeatFrequency : RepeatFrequency.NONE;
        this.repeatDays = repeatDays;
        this.repeatUntil = repeatUntil;
        this.repeatInterval = repeatInterval;
        this.time = time;
        this.excludedDates = excludedDates;

    }

    // --- Getters ---

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getGroupId() {
        return groupId;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isArchived() {
        return archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // --- Repeat getters ---

    public RepeatFrequency getRepeatFrequency() {
        return repeatFrequency;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public LocalDate getRepeatUntil() {
        return repeatUntil;
    }

    public Integer getRepeatInterval() {
        return repeatInterval;
    }

    public String getTime() {
        return time;
    }

    // --- Mutators ---

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Task title cannot be empty.");
        this.title = title.trim();
        touch();
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
        touch();
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        touch();
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
        touch();
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
        touch();
    }

    public void markDone() {
        this.done = true;
        touch();
    }

    public void markUndone() {
        this.done = false;
        touch();
    }

    public void setTime(String time) {
        this.time = time;
        touch();
    }

    // --- Repeat setters ---

    public void setRepeatFrequency(RepeatFrequency repeatFrequency) {
        this.repeatFrequency = repeatFrequency != null ? repeatFrequency : RepeatFrequency.NONE;
        touch();
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
        touch();
    }

    public void setRepeatUntil(LocalDate repeatUntil) {
        this.repeatUntil = repeatUntil;
        touch();
    }

    public void setRepeatInterval(Integer repeatInterval) {
        this.repeatInterval = repeatInterval;
        touch();
    }

    // --- Recurrence helpers ---

    /**
     * Returns true if this task should occur on the given date,
     * based on its repeat settings.
     */
    public boolean occursOn(LocalDate targetDate) {
        if (targetDate == null) {
            return false;
        }

        // Non-repeating: only on its deadline (if any)
        if (repeatFrequency == null || repeatFrequency == RepeatFrequency.NONE) {
            return deadline != null && deadline.equals(targetDate);
        }

        LocalDate start = getRepeatStartDate();
        if (start == null || targetDate.isBefore(start)) {
            return false;
        }

        if (repeatUntil != null && targetDate.isAfter(repeatUntil)) {
            return false;
        }

        if (excludedDates != null && excludedDates.contains(targetDate.toString())) {
            return false;
        }

        int interval = (repeatInterval != null && repeatInterval > 0) ? repeatInterval : 1;

        switch (repeatFrequency) {
            case DAILY -> {
                long days = ChronoUnit.DAYS.between(start, targetDate);
                return days >= 0 && days % interval == 0;
            }
            case WEEKLY -> {
                // Check weekday
                String dayCode = targetDate.getDayOfWeek().name().substring(0, 3); // MON, TUE, ...
                boolean dayMatches;
                if (repeatDays == null || repeatDays.isBlank()) {
                    // fall back: only same weekday as start date
                    String startDayCode = start.getDayOfWeek().name().substring(0, 3);
                    dayMatches = dayCode.equals(startDayCode);
                } else {
                    dayMatches = Arrays.stream(repeatDays.split(","))
                            .map(String::trim)
                            .anyMatch(code -> code.equalsIgnoreCase(dayCode));
                }
                if (!dayMatches)
                    return false;

                long weeks = ChronoUnit.WEEKS.between(start, targetDate);
                return weeks >= 0 && weeks % interval == 0;
            }
            case MONTHLY -> {
                // same day-of-month, every N months
                if (targetDate.getDayOfMonth() != start.getDayOfMonth()) {
                    return false;
                }
                long months = ChronoUnit.MONTHS.between(
                        start.withDayOfMonth(1),
                        targetDate.withDayOfMonth(1));
                return months >= 0 && months % interval == 0;
            }
            case YEARLY -> {
                // same month + day, every N years
                if (targetDate.getMonthValue() != start.getMonthValue()
                        || targetDate.getDayOfMonth() != start.getDayOfMonth()) {
                    return false;
                }
                long years = ChronoUnit.YEARS.between(start, targetDate);
                return years >= 0 && years % interval == 0;
            }
            default -> {
                return false;
            }
        }
    }

    public String getExcludedDates() {
        return excludedDates;
    }

    public void setExcludedDates(String excludedDates) {
        this.excludedDates = excludedDates;
        touch();
    }

    public void addExcludedDate(LocalDate date) {
        if (excludedDates == null || excludedDates.isBlank()) {
            excludedDates = date.toString();
        } else {
            excludedDates += "," + date;
        }
        touch();
    }

    /**
     * Base date from which the recurrence starts.
     * Prefer deadline; fall back to createdAt.
     */
    private LocalDate getRepeatStartDate() {
        if (deadline != null) {
            return deadline;
        }
        if (createdAt != null) {
            return createdAt.toLocalDate();
        }
        return null;
    }

    // --- Helpers ---

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Task))
            return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (done ? "[✔]" : "[ ]") + " " + title +
                (deadline != null ? " (due: " + deadline + ")" : "") +
                " [" + (priority != null ? priority : "-") + "]" +
                (archived ? " {archived}" : "") +
                (repeatFrequency != null && repeatFrequency != RepeatFrequency.NONE
                        ? " <repeats: " + repeatFrequency + ">"
                        : "");
    }
}
