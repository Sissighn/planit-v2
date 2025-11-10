package com.setayesh.planit.api;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.TaskService;
import com.setayesh.planit.core.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller exposing all task management features as HTTP endpoints.
 * Mirrors the same functionality as the CLI version but through REST.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Retrieve all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAll();
    }

    // Add a new task
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        LocalDate deadline = body.containsKey("deadline") ? LocalDate.parse(body.get("deadline")) : null;
        Priority priority = body.containsKey("priority")
                ? Priority.valueOf(body.get("priority").toUpperCase())
                : Priority.MEDIUM;

        Task newTask = new Task(title, deadline, priority);
        taskService.addTask(newTask);
        return ResponseEntity.ok(newTask);
    }

    // Mark a task as done
    @PutMapping("/{id}/done")
    public ResponseEntity<Void> markDone(@PathVariable UUID id) {
        taskService.markDone(id);
        return ResponseEntity.noContent().build();
    }

    // Mark a task as undone
    @PutMapping("/{id}/undone")
    public ResponseEntity<Void> markUndone(@PathVariable UUID id) {
        taskService.markUndone(id);
        return ResponseEntity.noContent().build();
    }

    // Edit an existing task
    @PutMapping("/{id}")
    public ResponseEntity<Void> editTask(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {

        String title = body.get("title");
        LocalDate deadline = body.containsKey("deadline") ? LocalDate.parse(body.get("deadline")) : null;
        Priority priority = body.containsKey("priority")
                ? Priority.valueOf(body.get("priority").toUpperCase())
                : null;

        taskService.editTask(id, title, deadline, priority);
        return ResponseEntity.noContent().build();
    }

    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // Archive a task
    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveTask(@PathVariable UUID id) {
        taskService.archiveTask(id);
        return ResponseEntity.noContent().build();
    }

    // Get all archived tasks
    @GetMapping("/archive")
    public List<Task> getArchivedTasks() {
        return taskService.loadArchive();
    }

    // Clear completed but non-archived tasks
    @DeleteMapping("/clear-completed")
    public ResponseEntity<Void> clearCompleted() {
        taskService.clearCompletedNotArchived();
        return ResponseEntity.noContent().build();
    }

    // Sort tasks by field (title, deadline, or priority)
    @GetMapping("/sorted")
    public ResponseEntity<List<Task>> getSortedTasks(@RequestParam(defaultValue = "priority") String by) {
        switch (by.toLowerCase()) {
            case "deadline" -> taskService.sortByDeadline();
            case "title" -> taskService.sortByTitle();
            default -> taskService.sortByPriority();
        }
        return ResponseEntity.ok(taskService.getAll());
    }
}
