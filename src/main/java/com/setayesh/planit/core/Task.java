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

    // --- Constructors ---

    // Default constructor for Jackson (and manual creation with defaults).
    public Task() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.done = false;
        this.archived = false;
    }

    // Minimal constructor with title only.
    public Task(String title) {
        this();
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Task title cannot be empty.");
        this.title = title.trim();
    }

    // Full constructor for explicit task creation.
    public Task(String title, LocalDate deadline, Priority priority) {
        this(); // sets id, timestamps, defaults

        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Task title cannot be empty.");

        this.title = title.trim();
        this.deadline = deadline;
        this.priority = priority;
    }

    // Jackson constructor for JSON deserialization
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
            @JsonProperty("updatedAt") LocalDateTime updatedAt) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.title = title;
        this.deadline = deadline;
        this.priority = priority;
        this.groupId = groupId;
        this.done = done;
        this.archived = archived;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.updatedAt = (updatedAt != null) ? updatedAt : LocalDateTime.now();
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

    // --- State changes ---

    public void markDone() {
        this.done = true;
        touch();
    }

    public void markUndone() {
        this.done = false;
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
                " [" + (priority != null ? priority : "-") + "]"
                +
                (archived ? " {archived}" : "");
    }
}
