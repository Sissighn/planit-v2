package com.setayesh.planit.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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
        setTitle(title);
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
