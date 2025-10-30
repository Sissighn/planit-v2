package com.setayesh.planit.core;

import java.time.LocalDate;

public class Task {
    private String title;
    private LocalDate deadline;
    private Priority priority;
    private boolean done;
    private boolean archived;

    public Task() {
    }

    public Task(String title, LocalDate deadline, Priority priority) {
        this.title = title;
        this.deadline = deadline;
        this.priority = priority;
        this.done = false;
        this.archived = false;

    }

    // Getters & Setters
    public String getTitle() {
        return title;
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

    // Done/Undone
    public void markDone() {
        this.done = true;
    }

    public void markUndone() {
        this.done = false;
    }

    // Setters for Edit
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    // Setter for Archive
    public void setArchived(boolean archived) {
        this.archived = archived;
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
