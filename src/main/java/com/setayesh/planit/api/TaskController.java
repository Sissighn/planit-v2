package com.setayesh.planit.api;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.TaskService;
import com.setayesh.planit.core.Priority;
import com.setayesh.planit.core.RecurrenceUtils;
import com.setayesh.planit.core.RepeatFrequency;
import com.setayesh.planit.storage.TaskInstanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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

    // --------------------------------------------------------------------
    // GET ALL TASKS
    // --------------------------------------------------------------------
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAll();
    }

    // --------------------------------------------------------------------
    // CREATE TASK (FULL RECURRENCE SUPPORT)
    // --------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Map<String, Object> body) {

        String title = body.getOrDefault("title", "").toString();

        // deadline
        String deadlineVal = (String) body.get("deadline");
        LocalDate deadline = (deadlineVal == null || deadlineVal.isBlank())
                ? null
                : LocalDate.parse(deadlineVal);

        // priority
        String prioVal = (String) body.get("priority");
        Priority prio = (prioVal == null || prioVal.isBlank())
                ? null
                : Priority.valueOf(prioVal.toUpperCase());

        Task newTask = new Task(title, deadline, prio);

        // startDate
        if (body.containsKey("startDate") && body.get("startDate") != null) {
            newTask.setStartDate(LocalDate.parse(body.get("startDate").toString()));
        }

        // recurrence
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

        // initial next occurrence berechnen
        List<LocalDate> empty = Collections.emptyList();
        LocalDate next = RecurrenceUtils.computeNextOccurrence(newTask, empty);
        newTask.setNextOccurrence(next);

        taskService.addTask(newTask);
        return ResponseEntity.ok(newTask);
    }

    // --------------------------------------------------------------------
    // UPDATE (PUT)
    // --------------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Void> editTask(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {

        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));

        // ---------------- TITLE ----------------
        if (body.containsKey("title") && body.get("title") != null) {
            task.setTitle(body.get("title").toString());
        }

        // ---------------- DEADLINE ----------------
        if (body.containsKey("deadline")) {
            Object d = body.get("deadline");
            task.setDeadline(
                    (d == null || d.toString().isBlank())
                            ? null
                            : LocalDate.parse(d.toString()));
        }

        // ---------------- PRIORITY ----------------
        if (body.containsKey("priority") && body.get("priority") != null) {
            task.setPriority(
                    Priority.valueOf(body.get("priority").toString().toUpperCase()));
        }

        // ---------------- DONE TOGGLE (one-time tasks only!) ----------------
        if (body.containsKey("done") && body.get("done") != null) {

            boolean doneFlag = Boolean.parseBoolean(body.get("done").toString());

            if (task.getRepeatFrequency() == RepeatFrequency.NONE) {
                // one-time → normal toggle
                if (doneFlag)
                    task.markDone();
                else
                    task.markUndone();
            }
            // recurring tasks → ignore here (handled by /complete/{date})
        }

        // ---------------- START DATE ----------------
        if (body.containsKey("startDate")) {
            Object sd = body.get("startDate");
            task.setStartDate(
                    (sd == null || sd.toString().isBlank())
                            ? null
                            : LocalDate.parse(sd.toString()));
        }

        // ---------------- REPEAT FREQ ----------------
        if (body.containsKey("repeatFrequency")) {
            Object f = body.get("repeatFrequency");
            task.setRepeatFrequency(
                    (f == null)
                            ? RepeatFrequency.NONE
                            : RepeatFrequency.valueOf(f.toString().toUpperCase()));
        }

        // ---------------- REPEAT DAYS ----------------
        if (body.containsKey("repeatDays")) {
            task.setRepeatDays((String) body.get("repeatDays"));
        }

        // ---------------- REPEAT UNTIL ----------------
        if (body.containsKey("repeatUntil")) {
            Object u = body.get("repeatUntil");
            task.setRepeatUntil(
                    (u == null || u.toString().isBlank())
                            ? null
                            : LocalDate.parse(u.toString()));
        }

        // ---------------- REPEAT INTERVAL ----------------
        if (body.containsKey("repeatInterval")) {
            Object v = body.get("repeatInterval");
            if (v == null) {
                task.setRepeatInterval(null);
            } else if (v instanceof Number num) {
                task.setRepeatInterval(num.intValue());
            }
        }

        // ---------------- TIME ----------------
        if (body.containsKey("time")) {
            task.setTime((String) body.get("time"));
        }

        // ---------------- EXCLUDED DATES ----------------
        if (body.containsKey("excludedDates")) {
            task.setExcludedDates((String) body.get("excludedDates"));
        }

        // ---------------- NEXT OCCURRENCE ----------------
        // ONLY RECURRENT tasks should have nextOccurrence!
        if (task.getRepeatFrequency() != RepeatFrequency.NONE) {
            List<LocalDate> completed = instanceRepo.findCompletedDates(id);
            LocalDate next = RecurrenceUtils.computeNextOccurrence(task, completed);
            task.setNextOccurrence(next);
        } else {
            // one-time tasks → nextOccurrence should NOT exist
            task.setNextOccurrence(null);
        }

        taskService.save();
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------------------
    // DELETE TASK
    // --------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------------------
    // ARCHIVE
    // --------------------------------------------------------------------
    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveTask(@PathVariable UUID id) {
        taskService.archiveTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/archive")
    public List<Task> getArchivedTasks() {
        return taskService.loadArchive();
    }

    // --------------------------------------------------------------------
    // COMPLETED INSTANCES (GET)
    // --------------------------------------------------------------------
    @GetMapping("/{id}/completed-instances")
    public ResponseEntity<List<String>> getCompletedInstances(@PathVariable UUID id) {
        List<LocalDate> dates = instanceRepo.findCompletedDates(id);
        return ResponseEntity.ok(dates.stream().map(LocalDate::toString).toList());
    }

    // --------------------------------------------------------------------
    // COMPLETE ONE INSTANCE (POST /complete/{date})
    // --------------------------------------------------------------------
    @PostMapping("/{id}/complete/{date}")
    public ResponseEntity<Void> markInstanceCompleted(
            @PathVariable UUID id,
            @PathVariable String date) {

        LocalDate d = LocalDate.parse(date);

        instanceRepo.markCompleted(id, d);

        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));

        List<LocalDate> completed = instanceRepo.findCompletedDates(id);
        LocalDate next = RecurrenceUtils.computeNextOccurrence(task, completed);
        task.setNextOccurrence(next);

        taskService.save();
        return ResponseEntity.ok().build();
    }

    // --------------------------------------------------------------------
    // DELETE ONE OCCURRENCE (POST /exclude/{date})
    // --------------------------------------------------------------------
    @PostMapping("/{id}/exclude/{date}")
    public ResponseEntity<Void> excludeOccurrence(
            @PathVariable UUID id,
            @PathVariable String date) {

        LocalDate d = LocalDate.parse(date);

        taskService.excludeDate(id, d);

        Task task = taskService.findById(id)
                .orElseThrow();
        List<LocalDate> completed = instanceRepo.findCompletedDates(id);
        LocalDate next = RecurrenceUtils.computeNextOccurrence(task, completed);
        task.setNextOccurrence(next);

        taskService.save();
        return ResponseEntity.ok().build();
    }

    // --------------------------------------------------------------------
    // DELETE FUTURE OCCURRENCES (PATCH /delete-future/{date})
    // --------------------------------------------------------------------
    @PatchMapping("/{id}/delete-future/{date}")
    public ResponseEntity<Void> deleteFutureOccurrences(
            @PathVariable UUID id,
            @PathVariable String date) {

        LocalDate d = LocalDate.parse(date);
        taskService.deleteFutureOccurrences(id, d);

        Task task = taskService.findById(id)
                .orElseThrow();
        List<LocalDate> completed = instanceRepo.findCompletedDates(id);
        LocalDate next = RecurrenceUtils.computeNextOccurrence(task, completed);
        task.setNextOccurrence(next);

        taskService.save();
        return ResponseEntity.ok().build();
    }

    // --------------------------------------------------------------------
    // DELETE WHOLE SERIES
    // --------------------------------------------------------------------
    @DeleteMapping("/{id}/delete-series")
    public ResponseEntity<Void> deleteSeries(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
