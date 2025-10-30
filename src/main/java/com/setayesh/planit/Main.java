package com.setayesh.planit;

import java.io.File;

import com.setayesh.planit.core.*;
import com.setayesh.planit.storage.*;
import com.setayesh.planit.ui.ConsoleUI;

/**
 * Entry point for PlanIT v2.
 * Initializes repositories and starts the console interface.
 */
public class Main {
    public static void main(String[] args) {
        String baseDirPath = System.getProperty("user.home") + File.separator + ".planit";

        JsonTaskRepository repo = new JsonTaskRepository(baseDirPath);
        TaskService service = new TaskService(repo);
        ConsoleUI ui = new ConsoleUI(service);

        ui.start();
    }
}
