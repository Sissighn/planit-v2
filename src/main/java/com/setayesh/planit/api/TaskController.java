package com.setayesh.planit.api;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.TaskService;
import com.setayesh.planit.storage.DatabaseTaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController() {
        // ⚙️ Temporär direkte Instanziierung (später Dependency Injection)
        this.service = new TaskService(new DatabaseTaskRepository());
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return service.getAll();
    }

    @PostMapping
    public Task addTask(@RequestBody Task task) {
        service.addTask(task);
        return task;
    }

    @PutMapping("/{id}/done")
    public void markDone(@PathVariable UUID id) {
        service.markDone(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable UUID id) {
        service.deleteTask(id);
    }
}
