package com.setayesh.planit.storage;

import com.setayesh.planit.core.Task;
import java.util.*;

// Abstraction for task persistence. Allows swapping JSON, in-memory, DB, etc.
public interface TaskRepository {

    // Returns all tasks. May be empty but never null.
    List<Task> findAll();

    void saveAll(List<Task> tasks);

    List<Task> loadArchive();

    void saveArchive(List<Task> archive);
}