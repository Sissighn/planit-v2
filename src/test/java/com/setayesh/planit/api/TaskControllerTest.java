package com.setayesh.planit.api;

import com.setayesh.planit.core.Task;
import com.setayesh.planit.core.TaskService;
import com.setayesh.planit.storage.InMemoryTaskRepository;
import com.setayesh.planit.storage.TaskInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskControllerTest {

    private TaskController controller;

    @BeforeEach
    void setUp() {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        TaskInstanceRepository instances = new TaskInstanceRepository(
                "jdbc:h2:mem:task-controller-test;DB_CLOSE_DELAY=-1");
        controller = new TaskController(service, instances);
    }

    @Test
    void addTaskAcceptsNumericGroupId() {
        Task created = controller.addTask(Map.of(
                "title", "Grouped task",
                "groupId", 7)).getBody();

        assertEquals(7L, created.getGroupId());
    }

    @Test
    void addTaskAcceptsStringGroupIdFromHtmlSelect() {
        Task created = controller.addTask(Map.of(
                "title", "Grouped task",
                "groupId", "8")).getBody();

        assertEquals(8L, created.getGroupId());
    }

    @Test
    void editTaskCanClearGroup() {
        Task created = controller.addTask(Map.of(
                "title", "Grouped task",
                "groupId", 9)).getBody();
        Map<String, Object> update = new HashMap<>();
        update.put("groupId", null);

        controller.editTask(created.getId(), update);

        assertNull(created.getGroupId());
    }
}
