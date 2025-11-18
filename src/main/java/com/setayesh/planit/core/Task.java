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
    private LocalDate startDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String time;
    private String excludedDates;
    private RepeatFrequency repeatFrequency;
    private String repeatDays;
    private LocalDate repeatUntil;
    private Integer repeatInterval;

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
        this.startDate = deadline;

    }

    // --- JSON Constructor ---
    @JsonCreator
    public Task(
            @JsonProperty("id") UUID id,
            @JsonProperty("title") String title,
            @JsonProperty("deadline") LocalDate deadline,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("groupId") Long groupId,
            @JsonProperty("done") Boolean done,
            @JsonProperty("archived") Boolean archived,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt,
            @JsonProperty("repeatFrequency") RepeatFrequency repeatFrequency,
            @JsonProperty("repeatDays") String repeatDays,
            @JsonProperty("repeatUntil") LocalDate repeatUntil,
            @JsonProperty("excludedDates") String excludedDates,
            @JsonProperty("time") String time,
            @JsonProperty("repeatInterval") Integer repeatInterval,
            @JsonProperty("startDate") LocalDate startDate) {

        this.id = (id != null ? id : UUID.randomUUID());
        this.title = title;

        // (1) deadline
        this.deadline = deadline;

        // (2) startDate fallback
        if (startDate != null) {
            this.startDate = startDate;
        } else {
            this.startDate = deadline; // fallback
        }

        // CreatedAt
        this.createdAt = (createdAt != null ? createdAt : LocalDateTime.now());
        this.updatedAt = (updatedAt != null ? updatedAt : this.createdAt);

        this.priority = priority;
        this.groupId = groupId;

        this.done = (done != null ? done : false);
        this.archived = (archived != null ? archived : false);

        this.repeatFrequency = (repeatFrequency != null ? repeatFrequency : RepeatFrequency.NONE);
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

    public LocalDate getStartDate() {
        return startDate;
    }

    // --- Setters ---

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Task title cannot be empty.");
        this.title = title.trim();
        touch();
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;

        if (this.startDate == null) {
            this.startDate = deadline;
        }

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

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        touch();
    }

    // -------------------------------------------------------
    // Recurrence Logic
    // -------------------------------------------------------
    public boolean occursOn(LocalDate date) {
        if (date == null)
            return false;

        // non-recurring
        if (repeatFrequency == null || repeatFrequency == RepeatFrequency.NONE) {
            return deadline != null && deadline.equals(date);
        }

        LocalDate start = (startDate != null ? startDate : createdAt.toLocalDate());

        if (date.isBefore(start))
            return false;
        if (repeatUntil != null && date.isAfter(repeatUntil))
            return false;

        if (excludedDates != null && excludedDates.contains(date.toString()))
            return false;

        int interval;
        if (repeatInterval != null && repeatInterval > 0) {
            interval = repeatInterval;
        } else {
            interval = 1;
        }

        switch (repeatFrequency) {

            case DAILY -> {
                long diff = ChronoUnit.DAYS.between(start, date);
                return diff >= 0 && diff % interval == 0;
            }

            case WEEKLY -> {
                String dw = date.getDayOfWeek().name().substring(0, 3);
                boolean matches;

                if (repeatDays == null || repeatDays.isBlank()) {
                    matches = dw.equals(start.getDayOfWeek().name().substring(0, 3));
                } else {
                    matches = Arrays.stream(repeatDays.split(","))
                            .anyMatch(d -> d.trim().equalsIgnoreCase(dw));
                }

                if (!matches)
                    return false;

                long weeks = ChronoUnit.WEEKS.between(start, date);
                return weeks >= 0 && weeks % interval == 0;
            }

            case MONTHLY -> {
                if (date.getDayOfMonth() != start.getDayOfMonth())
                    return false;
                long months = ChronoUnit.MONTHS.between(
                        start.withDayOfMonth(1),
                        date.withDayOfMonth(1));
                return months >= 0 && months % interval == 0;
            }

            case YEARLY -> {
                if (date.getMonthValue() != start.getMonthValue()
                        || date.getDayOfMonth() != start.getDayOfMonth())
                    return false;

                long years = ChronoUnit.YEARS.between(start, date);
                return years >= 0 && years % interval == 0;
            }
            case CUSTOM -> {
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    public LocalDate computeNextOccurrence() {
        if (repeatFrequency == null || repeatFrequency == RepeatFrequency.NONE) {
            // non-recurring: nächste "Instanz" ist einfach die Deadline
            return deadline;
        }

        LocalDate startBase = getRepeatStartDate();
        if (startBase == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate cursor = !today.isBefore(startBase) ? today : startBase;

        // Suche maximal 2 Jahre in die Zukunft, um Endlosschleifen zu vermeiden
        for (int i = 0; i < 730; i++) {
            if (occursOn(cursor)) {
                return cursor;
            }
            cursor = cursor.plusDays(1);
        }
        return null;
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

    private LocalDate getRepeatStartDate() {
        if (startDate != null) {
            return startDate;
        }
        if (deadline != null) {
            return deadline;
        }
        if (createdAt != null) {
            return createdAt.toLocalDate();
        }
        return null;
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
