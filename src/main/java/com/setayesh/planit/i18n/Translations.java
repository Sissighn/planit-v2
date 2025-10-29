package com.setayesh.planit.i18n;

import java.util.HashMap;
import java.util.Map;
import com.setayesh.planit.ui.UIHelper;

/**
 * Provides English and German translations for all UI text.
 * Keeps UIHelper clean and supports easy multi-language scaling.
 */
public class Translations {

    private static final Map<String, String[]> texts = new HashMap<>();

    static {
        // GENERAL
        texts.put("welcome", new String[] {
                "Welcome to your To-Do List!",
                "Willkommen zu deiner To-Do-Liste!"
        });
        texts.put("no_tasks", new String[] {
                "No tasks found.",
                "Keine Aufgaben gefunden."
        });
        texts.put("goodbye", new String[] {
                "Goodbye!",
                "Auf Wiedersehen!"
        });
        texts.put("invalid_choice", new String[] {
                "Invalid choice! Please choose a number between %d and %d.",
                "Ungültige Auswahl! Bitte wähle eine Zahl zwischen %d und %d."
        });
        texts.put("please_number", new String[] {
                "Please enter a valid number!",
                "Bitte gib eine gültige Zahl ein!"
        });
        texts.put("saving_error", new String[] {
                "An error occurred while saving your tasks: ",
                "Beim Speichern der Aufgaben ist ein Fehler aufgetreten: "
        });

        // ADD TASK
        texts.put("enter_new", new String[] {
                "Enter the new task: ",
                "Neue Aufgabe eingeben: "
        });
        texts.put("empty_task", new String[] {
                "Task cannot be empty!",
                "Aufgabe darf nicht leer sein!"
        });
        texts.put("add_deadline", new String[] {
                "Enter a deadline (dd.MM.yyyy) or press Enter to skip: ",
                "Gib ein Fälligkeitsdatum ein (dd.MM.yyyy) oder Enter zum Überspringen: "
        });
        texts.put("add_priority", new String[] {
                "Enter priority (1=High, 2=Medium, 3=Low) or press Enter to skip: ",
                "Gib die Priorität ein (1=Hoch, 2=Mittel, 3=Niedrig) oder Enter zum Überspringen: "
        });
        texts.put("task_added", new String[] {
                "Task added successfully!",
                "Aufgabe erfolgreich hinzugefügt!"
        });

        // DONE / UNDONE
        texts.put("no_to_mark", new String[] {
                "No tasks to mark as done.",
                "Keine Aufgaben zum Markieren vorhanden."
        });
        texts.put("enter_num_mark", new String[] {
                "Enter the number of the task to mark/unmark (or 0 to cancel): ",
                "Gib die Nummer der Aufgabe ein, um sie als erledigt oder offen zu markieren (oder 0 zum Abbrechen): "
        });
        texts.put("marked_done", new String[] {
                "Task marked as done!",
                "Aufgabe als erledigt markiert!"
        });
        texts.put("marked_undone", new String[] {
                "Task marked as not done!",
                "Aufgabe als offen markiert!"
        });

        // SETTINGS
        texts.put("settings_title", new String[] {
                "=== Settings ===",
                "=== Einstellungen ==="
        });
        texts.put("settings_lang", new String[] {
                "1 - Language",
                "1 - Sprache"
        });
        texts.put("settings_back", new String[] {
                "2 - Back",
                "2 - Zurück"
        });
        texts.put("choose_lang", new String[] {
                "Choose a language: 1 - English, 2 - Deutsch (or 0 to cancel): ",
                "Wähle eine Sprache: 1 - English, 2 - Deutsch (oder 0 zum Abbrechen): "
        });
    }

    /**
     * Returns the translation for a given key and language.
     * Falls back to the key itself if missing.
     */
    public static String get(String key, UIHelper.Language lang) {
        String[] arr = texts.get(key);
        if (arr == null)
            return key; // fallback if missing
        return arr[lang == UIHelper.Language.EN ? 0 : 1];
    }
}
