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

                // EDIT TASK
                texts.put("edit_title", new String[] {
                                "=== Edit Task ===",
                                "=== Aufgabe bearbeiten ==="
                });
                texts.put("edit_choose", new String[] {
                                "Select a task to edit:",
                                "Wähle eine Aufgabe zum Bearbeiten:"
                });
                texts.put("edit_enter_num", new String[] {
                                "Enter the number of the task: ",
                                "Gib die Nummer der Aufgabe ein: "
                });
                texts.put("edit_new_title", new String[] {
                                "New title (press Enter to skip): ",
                                "Neuer Titel (Enter zum Überspringen): "
                });
                texts.put("edit_new_deadline", new String[] {
                                "New deadline (dd.MM.yyyy, Enter to skip): ",
                                "Neues Fälligkeitsdatum (dd.MM.yyyy, Enter zum Überspringen): "
                });
                texts.put("edit_new_priority", new String[] {
                                "New priority (1=High, 2=Medium, 3=Low, Enter to skip): ",
                                "Neue Priorität (1=Hoch, 2=Mittel, 3=Niedrig, Enter zum Überspringen): "
                });
                texts.put("edit_success", new String[] {
                                "Task updated successfully!",
                                "Aufgabe erfolgreich aktualisiert!"
                });
                texts.put("invalid_date", new String[] {
                                "Invalid date format!",
                                "Ungültiges Datumsformat!"
                });
                texts.put("invalid_priority", new String[] {
                                "Invalid priority number!",
                                "Ungültige Prioritätsnummer!"
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

                // DELETE TASK
                texts.put("no_to_delete", new String[] {
                                "No tasks to delete.",
                                "Keine Aufgaben zum Löschen."
                });
                texts.put("enter_num_delete", new String[] {
                                "Enter the number of the task to delete (or 0 to cancel): ",
                                "Gib die Nummer der Aufgabe zum Löschen ein (oder 0 zum Abbrechen): "
                });
                texts.put("task_deleted", new String[] {
                                "Task deleted: ",
                                "Aufgabe gelöscht: "
                });
                texts.put("deletion_cancel", new String[] {
                                "Deletion cancelled.",
                                "Löschen abgebrochen."
                });

                // SORTING
                texts.put("sort_menu_title", new String[] {
                                "=== Sorting ===",
                                "=== Sortierung ==="
                });
                texts.put("sort_choose", new String[] {
                                "Choose a sorting option:",
                                "Wähle eine Sortieroption:"
                });
                texts.put("sort_priority", new String[] {
                                "1 - By Priority",
                                "1 - Nach Priorität"
                });
                texts.put("sort_deadline", new String[] {
                                "2 - By Deadline",
                                "2 - Nach Fälligkeitsdatum"
                });
                texts.put("sort_created", new String[] {
                                "3 - By Created Date",
                                "3 - Nach Erstellungsdatum"
                });
                texts.put("sort_alpha", new String[] {
                                "4 - Alphabetically",
                                "4 - Alphabetisch"
                });
                texts.put("sort_back", new String[] {
                                "5 - Back",
                                "5 - Zurück"
                });
                texts.put("sort_done", new String[] {
                                "Tasks sorted successfully!",
                                "Aufgaben erfolgreich sortiert!"
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
                texts.put("lang_set", new String[] {
                                "Language set to English.",
                                "Sprache auf Deutsch gesetzt."
                });

                // ARCHIVE
                texts.put("archive_title", new String[] {
                                "=== Archive Task ===",
                                "=== Aufgabe archivieren ==="
                });
                texts.put("archive_enter_num", new String[] {
                                "Enter the number of the task to archive (or 0 to cancel): ",
                                "Gib die Nummer der Aufgabe ein, um sie zu archivieren (oder 0 zum Abbrechen): "
                });
                texts.put("archived_success", new String[] {
                                "Task successfully archived!",
                                "Aufgabe erfolgreich archiviert!"
                });
                texts.put("archive_view", new String[] {
                                "=== Archived Tasks ===",
                                "=== Archivierte Aufgaben ==="
                });
                texts.put("archive_empty", new String[] {
                                "No archived tasks yet.",
                                "Noch keine archivierten Aufgaben."
                });
                texts.put("press_enter", new String[] {
                                "Press Enter to return...",
                                "Drücke Enter, um zurückzukehren..."
                });
                texts.put("tasks_cleared", new String[] {
                                "Completed tasks removed.",
                                "Erledigte Aufgaben gelöscht."
                });
        }

        public static String get(String key, UIHelper.Language lang) {
                String[] arr = texts.get(key);
                if (arr == null)
                        return key;
                return arr[lang == UIHelper.Language.EN ? 0 : 1];
        }
}
