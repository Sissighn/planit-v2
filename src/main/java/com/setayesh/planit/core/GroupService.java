package com.setayesh.planit.core;

import com.setayesh.planit.storage.DatabaseGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final DatabaseGroupRepository repo;

    public GroupService(DatabaseGroupRepository repo) {
        this.repo = repo;
    }

    public List<Group> getAll() {
        return repo.findAll();
    }

    public Group addGroup(Group group) {
        // ID kommt jetzt aus H2 (IDENTITY), NICHT mehr manuell setzen
        group.setId(null);
        return repo.save(group);
    }

    public Group updateGroup(Group group) {
        return repo.save(group);
    }

    public void deleteGroup(Long id) {
        repo.delete(id);
    }

    public Group getById(Long id) {
        return repo.findById(id);
    }
}
