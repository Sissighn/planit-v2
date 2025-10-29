package com.setayesh.planit.core;

import java.time.LocalDate;

public class Task {
    private String title;
    private LocalDate deadline;
    private Priority priority;
    private boolean done;

    public Task(String title, LocalDate deadline, Priority priority) {
        this.title = title;
        this.deadline = deadline;
        this.priority = priority;
        this.done = false;
    }

    // Getters & Setters
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public Priority getPriority() { return priority; }
    public boolean isDone() { return done; }

    public void markDone() { this.done = true; }
    public void markUndone() { this.done = false; }

    @Override
    public String toString() {
        return (done ? "[✔]" : "[ ]") + " " + title + 
               (deadline != null ? " (due: " + deadline + ")" : "") +
               " [" + priority + "]";
    }
}

