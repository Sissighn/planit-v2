package com.setayesh.planit.api;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.TaskService;
import com.setayesh.planit.storage.TaskInstanceRepository;
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
    private final TaskInstanceRepository instanceRepo;

    @Autowired
    public TaskController(TaskService taskService, TaskInstanceRepository instanceRepo) {
        this.taskService = taskService;
        this.instanceRepo = instanceRepo;
    }

    // --------------------------------------------------------
    // GET ALL TASKS
    // --------------------------------------------------------
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAll();
    }

    // --------------------------------------------------------
    // CREATE TASK (with recurrence)
    // --------------------------------------------------------
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Map<String, Object> body) {

        String title = body.getOrDefault("title", "").toString();

        String deadlineValue = (String) body.get("deadline");
        LocalDate deadline = (deadlineValue == null || deadlineValue.isBlank())
                ? null
                : LocalDate.parse(deadlineValue);

        String priorityValue = (String) body.get("priority");
        Priority priority = (priorityValue == null || priorityValue.isBlank())
                ? null
                : Priority.valueOf(priorityValue.toUpperCase());

        Task newTask = new Task(title, deadline, priority);

        // startDate
        if (body.containsKey("startDate") && body.get("startDate") != null) {
            newTask.setStartDate(LocalDate.parse(body.get("startDate").toString()));
        }

        // Recurrence fields
        if (body.containsKey("repeatFrequency") && body.get("repeatFrequency") != null) {
            newTask.setRepeatFrequency(
                    RepeatFrequency.valueOf(body.get("repeatFrequency").toString().toUpperCase()));
        }

        if (body.containsKey("repeatDays")) {
            newTask.setRepeatDays((String) body.get("repeatDays"));
        }

        if (body.containsKey("repeatUntil") && body.get("repeatUntil") != null) {
            newTask.setRepeatUntil(LocalDate.parse(body.get("repeatUntil").toString()));
        }

        if (body.containsKey("repeatInterval") && body.get("repeatInterval") != null) {
            Object interval = body.get("repeatInterval");
            if (interval instanceof Number num) {
                newTask.setRepeatInterval(num.intValue());
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
    // EDIT TASK (full recurrence support)
    // --------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Void> editTask(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {

        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));

        // title
        if (body.containsKey("title") && body.get("title") != null) {
            task.setTitle(body.get("title").toString());
        }

        // deadline
        if (body.containsKey("deadline")) {
            Object d = body.get("deadline");
            if (d == null || d.toString().isBlank()) {
                task.setDeadline(null);
            } else {
                task.setDeadline(LocalDate.parse(d.toString()));
            }
        }

        // priority
        if (body.containsKey("priority") && body.get("priority") != null) {
            task.setPriority(Priority.valueOf(body.get("priority").toString().toUpperCase()));
        }

        // startDate
        if (body.containsKey("startDate")) {
            Object sd = body.get("startDate");
            if (sd == null || sd.toString().isBlank()) {
                task.setStartDate(null);
            } else {
                task.setStartDate(LocalDate.parse(sd.toString()));
            }
        }

        // repeatFrequency
        if (body.containsKey("repeatFrequency")) {
            Object f = body.get("repeatFrequency");
            if (f == null) {
                task.setRepeatFrequency(RepeatFrequency.NONE);
            } else {
                task.setRepeatFrequency(RepeatFrequency.valueOf(f.toString().toUpperCase()));
            }
        }

        // repeatDays
        if (body.containsKey("repeatDays")) {
            task.setRepeatDays((String) body.get("repeatDays"));
        }

        // repeatUntil
        if (body.containsKey("repeatUntil")) {
            Object u = body.get("repeatUntil");
            if (u == null || u.toString().isBlank()) {
                task.setRepeatUntil(null);
            } else {
                task.setRepeatUntil(LocalDate.parse(u.toString()));
            }
        }

        // repeatInterval
        if (body.containsKey("repeatInterval")) {
            Object val = body.get("repeatInterval");
            if (val == null) {
                task.setRepeatInterval(null);
            } else if (val instanceof Number num) {
                task.setRepeatInterval(num.intValue());
            }
        }

        // time
        if (body.containsKey("time")) {
            task.setTime((String) body.get("time"));
        }

        // excludedDates
        if (body.containsKey("excludedDates")) {
            task.setExcludedDates((String) body.get("excludedDates"));
        }

        taskService.save();
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
    // DELETE TASK
    // --------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // DELETE ONE OCCURRENCE (exclude-date)
    // --------------------------------------------------------
    @PutMapping("/{id}/exclude-date")
    public ResponseEntity<Void> excludeDate(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {

        LocalDate date = LocalDate.parse(body.get("date"));
        taskService.excludeDate(id, date);

        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------
    // DELETE FUTURE OCCURRENCES
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
    // TASKS FOR A DATE
    // ---------------------------------------------------------
    @GetMapping("/for-date")
    public ResponseEntity<List<Task>> getTasksForDate(@RequestParam String date) {
        try {
            LocalDate parsed = LocalDate.parse(date);
            return ResponseEntity.ok(taskService.getTasksForDate(parsed));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ---------------------------------------------------------
    // COMPLETED INSTANCES
    // ---------------------------------------------------------
    @GetMapping("/{id}/completed-instances")
    public ResponseEntity<List<String>> getCompletedInstances(@PathVariable UUID id) {
        List<LocalDate> dates = instanceRepo.findCompletedDates(id);
        return ResponseEntity.ok(dates.stream().map(LocalDate::toString).toList());
    }

    @PutMapping("/{id}/done-on")
    public ResponseEntity<Void> markDoneOnDate(
            @PathVariable UUID id,
            @RequestParam String date) {

        LocalDate d = LocalDate.parse(date);
        taskService.markInstanceCompleted(id, d);

        return ResponseEntity.ok().build();
    }

    // ---------------------------------------------------------
    // TODAY'S TASKS
    // ---------------------------------------------------------
    @GetMapping("/today")
    public List<Task> getToday() {
        return taskService.getTasksForDate(LocalDate.now());
    }
}
