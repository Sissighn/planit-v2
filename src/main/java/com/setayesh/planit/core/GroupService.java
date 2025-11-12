package com.setayesh.planit.core;

import com.setayesh.planit.storage.GroupRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GroupService {
    private final GroupRepository repo;
    private final List<Group> groups;

    public GroupService(GroupRepository repo) {
        this.repo = Objects.requireNonNull(repo);
        this.groups = new ArrayList<>(repo.findAll());
    }

    public List<Group> getAll() {
        return Collections.unmodifiableList(groups);
    }

    public void addGroup(Group group) {
        groups.add(group);
        save();
    }

    public void deleteGroup(Long id) {
        groups.removeIf(g -> g.getId().equals(id));
        save();
    }

    public void save() {
        repo.saveAll(groups);
    }
}
