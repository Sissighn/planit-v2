package com.setayesh.planit;

import java.util.List;

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

        TaskRepository repo = new DatabaseTaskRepository();
        TaskService service = new TaskService(repo);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                service.save();

                List<Task> archive = service.loadArchive(); // aus DB oder JSON holen
                if (archive != null && !archive.isEmpty()) {
                    repo.saveArchive(archive);
                }

                System.out.println("✅ All data saved safely.");
            } catch (Exception e) {
                System.err.println("⚠️ Error while saving on exit: " + e.getMessage());
            }
        }));

        ConsoleUI ui = new ConsoleUI(service);
        ui.start();
    }
}
