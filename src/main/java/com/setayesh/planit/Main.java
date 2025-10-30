package com.setayesh.planit;

import java.io.File;

import com.setayesh.planit.core.*;
import com.setayesh.planit.storage.*;
import com.setayesh.planit.ui.ConsoleUI;
import com.setayesh.planit.ui.UIHelper;

/**
 * Entry point for PlanIT v2.
 * Initializes repositories and starts the console interface.
 */
public class Main {
    public static void main(String[] args) {

        UIHelper.loadSettings();
        UIHelper.loadLanguageFromFile();
        UIHelper.loadDashboardMode();

        String baseDirPath = System.getProperty("user.home") + File.separator + ".planit";

        JsonTaskRepository repo = new JsonTaskRepository(baseDirPath);
        TaskService service = new TaskService(repo);
        ConsoleUI ui = new ConsoleUI(service);

        ui.start();
    }
}
