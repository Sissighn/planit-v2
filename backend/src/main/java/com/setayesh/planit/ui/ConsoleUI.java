package com.setayesh.planit.ui;

import com.setayesh.planit.core.*;
import java.util.List;

public class ConsoleUI {
    private final TaskService service;
    private final InputHandler input;
    private final MenuManager menu;

    public ConsoleUI(TaskService service) {
        this.service = service;
        this.input = new InputHandler();
        this.menu = new MenuManager(service, input);
    }

    public void start() {
        UIHelper.printHeader(UIHelper.t("welcome"));
        boolean running = true;

        while (running) {
            UIHelper.printPageHeader("home");

            List<Task> tasks = service.getAll();
            List<Task> archived = service.loadArchive();
            long completedCount = tasks.stream().filter(Task::isDone).count();

            UIHelper.printDashboard(archived.size(), (int) completedCount, tasks.size());
            TodoPrinter.printTodoList(tasks);

            String choice = input.readLine("> ");
            running = menu.handleMainMenu(choice);
        }

        System.out.println(Colors.PASTEL_GREEN + UIHelper.t("goodbye") + Colors.RESET);
        service.save();
    }
}
