package com.setayesh.planit.api;

import com.setayesh.planit.core.Group;
import com.setayesh.planit.core.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:5173", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
public class GroupController {
    private final GroupService service;

    public GroupController(GroupService service) {
        this.service = service;
    }

    @GetMapping
    public List<Group> getAllGroups() {
        return service.getAll();
    }

    @PostMapping
    public Group addGroup(@RequestBody Group group) {
        if (group.getId() != null) {
            throw new IllegalArgumentException("Use PUT for updating existing groups.");
        }
        return service.addGroup(group);
    }

    @PutMapping("/{id}")
    public Group updateGroup(@PathVariable Long id, @RequestBody Group updatedGroup) {

        Group existing = service.getById(id);
        if (existing == null) {
            throw new RuntimeException("Group not found: " + id);
        }

        existing.setName(updatedGroup.getName());
        return service.updateGroup(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        service.deleteGroup(id);
    }
}
