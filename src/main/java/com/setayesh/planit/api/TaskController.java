package com.setayesh.planit.api;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.TaskService;
import com.setayesh.planit.core.Priority;
import com.setayesh.planit.core.RepeatFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // --------------------------------------------------------
    // GET ALL TASKS
    // --------------------------------------------------------
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAll();
    }

    // --------------------------------------------------------
    // CREATE TASK (supports recurrence)
    // --------------------------------------------------------
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Map<String, Object> body) {

        String title = (String) body.get("title");

        String deadlineValue = (String) body.get("deadline");
        LocalDate deadline = (deadlineValue == null || deadlineValue.isBlank())
                ? null
                : LocalDate.parse(deadlineValue);

        String priorityValue = (String) body.get("priority");
        Priority priority = (priorityValue == null || priorityValue.isBlank())
                ? null
                : Priority.valueOf(priorityValue.toUpperCase());

        Task newTask = new Task(title, deadline, priority);

        // Recurrence fields
        if (body.containsKey("repeatFrequency") && body.get("repeatFrequency") != null) {
            newTask.setRepeatFrequency(
                    RepeatFrequency.valueOf(((String) body.get("repeatFrequency")).toUpperCase()));
        }

        if (body.containsKey("repeatDays")) {
            newTask.setRepeatDays((String) body.get("repeatDays"));
        }

        if (body.containsKey("repeatUntil") && body.get("repeatUntil") != null) {
            newTask.setRepeatUntil(LocalDate.parse((String) body.get("repeatUntil")));
        }

        if (body.containsKey("repeatInterval")) {
            Object interval = body.get("repeatInterval");
            if (interval instanceof Number number) {
                newTask.setRepeatInterval(number.intValue());
            }
        }

        if (body.containsKey("time")) {
            newTask.setTime((String) body.get("time"));
        }

        if (body.containsKey("excludedDates")) {
            newTask.setExcludedDates((String) body.get("excludedDates"));
        }

        taskService.addTask(newTask);
        return ResponseEntity.ok(newTask);
    }

    // --------------------------------------------------------
    // EDIT TASK (supports recurrence)
    // --------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Void> editTask(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {

        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));

        // Regular fields
        if (body.containsKey("title"))
            task.setTitle((String) body.get("title"));

        if (body.containsKey("deadline")) {
            Object d = body.get("deadline");
            if (d == null || ((String) d).isBlank()) {
                task.setDeadline(null);
            } else {
                task.setDeadline(LocalDate.parse((String) d));
            }
        }

        if (body.containsKey("priority") && body.get("priority") != null) {
            task.setPriority(Priority.valueOf(((String) body.get("priority")).toUpperCase()));
        }

        // Recurrence fields
        if (body.containsKey("repeatFrequency")) {
            Object f = body.get("repeatFrequency");
            if (f == null)
                task.setRepeatFrequency(null);
            else
                task.setRepeatFrequency(RepeatFrequency.valueOf(((String) f).toUpperCase()));
        }

        if (body.containsKey("repeatDays")) {
            task.setRepeatDays((String) body.get("repeatDays"));
        }

        if (body.containsKey("repeatUntil")) {
            Object u = body.get("repeatUntil");
            if (u == null) {
                task.setRepeatUntil(null);
            } else {
                task.setRepeatUntil(LocalDate.parse((String) u));
            }
        }

        if (body.containsKey("repeatInterval")) {
            Object val = body.get("repeatInterval");
            if (val == null)
                task.setRepeatInterval(null);
            else if (val instanceof Number number)
                task.setRepeatInterval(number.intValue());
        }

        if (body.containsKey("time")) {
            task.setTime((String) body.get("time"));
        }

        if (body.containsKey("excludedDates")) {
            task.setExcludedDates((String) body.get("excludedDates"));
        }

        taskService.save(); // everything persisted
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // DONE / UNDONE
    // --------------------------------------------------------
    @PutMapping("/{id}/done")
    public ResponseEntity<Void> markDone(@PathVariable UUID id) {
        taskService.markDone(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/undone")
    public ResponseEntity<Void> markUndone(@PathVariable UUID id) {
        taskService.markUndone(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // DELETE
    // --------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // DELETE ONE OCCURRENCE
    // --------------------------------------------------------
    @PutMapping("/{id}/exclude-date")
    public ResponseEntity<Void> excludeDate(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {

        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        LocalDate date = LocalDate.parse(body.get("date"));
        task.addExcludedDate(date);

        taskService.save();
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // DELETE ALL FUTURE OCCURRENCES
    // --------------------------------------------------------
    @PutMapping("/{id}/cut-off")
    public ResponseEntity<Void> deleteFutureOccurrences(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {

        LocalDate fromDate = LocalDate.parse(body.get("date"));
        taskService.deleteFutureOccurrences(id, fromDate);

        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // ARCHIVE
    // --------------------------------------------------------
    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveTask(@PathVariable UUID id) {
        taskService.archiveTask(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // GET ARCHIVE
    // --------------------------------------------------------
    @GetMapping("/archive")
    public List<Task> getArchivedTasks() {
        return taskService.loadArchive();
    }

    // --------------------------------------------------------
    // CLEAR COMPLETED
    // --------------------------------------------------------
    @DeleteMapping("/clear-completed")
    public ResponseEntity<Void> clearCompleted() {
        taskService.clearCompletedNotArchived();
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // SORTING
    // --------------------------------------------------------
    @GetMapping("/sorted")
    public ResponseEntity<List<Task>> getSortedTasks(
            @RequestParam(defaultValue = "priority") String by) {

        switch (by.toLowerCase()) {
            case "deadline" -> taskService.sortByDeadline();
            case "title" -> taskService.sortByTitle();
            default -> taskService.sortByPriority();
        }

        return ResponseEntity.ok(taskService.getAll());
    }

    // ---------------------------------------------------------
    // Tasks for a specific date (includes repeating tasks)
    // ---------------------------------------------------------
    @GetMapping("/for-date")
    public ResponseEntity<List<Task>> getTasksForDate(@RequestParam String date) {
        try {
            LocalDate parsed = LocalDate.parse(date);
            List<Task> result = taskService.getTasksForDate(parsed);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // invalid date format
        }
    }

}
