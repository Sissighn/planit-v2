package com.setayesh.planit.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setayesh.planit.core.Group;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Repository
public class GroupRepository {
    private static final String FILE_PATH = "data/groups.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Group> findAll() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists())
                return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Group>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveAll(List<Group> groups) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), groups);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
