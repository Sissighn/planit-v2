package com.setayesh.planit;

import com.setayesh.planit.core.*;
import com.setayesh.planit.storage.*;
import com.setayesh.planit.ui.ConsoleUI;

/**
 * Entry point for PlanIt v2.
 * Initializes repositories and starts the console interface.
 */
public class Main {
    public static void main(String[] args) {
        String filePath = System.getProperty("user.home") + "/planit_tasks.json";

        JsonTaskRepository repo = new JsonTaskRepository(filePath);
        TaskService service = new TaskService(repo);

        ConsoleUI ui = new ConsoleUI(service);
        ui.start();
    }
}
