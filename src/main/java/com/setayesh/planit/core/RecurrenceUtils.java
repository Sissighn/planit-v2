package com.setayesh.planit.core;

import java.time.LocalDate;
import java.util.*;

public class RecurrenceUtils {

    public static LocalDate computeNextOccurrence(Task t, List<LocalDate> completedDates) {

        // non-recurring → next occurrence = deadline
        if (t.getRepeatFrequency() == RepeatFrequency.NONE) {
            return t.getDeadline();
        }

        LocalDate today = LocalDate.now();

        // Startdatum hat oberste Priorität
        LocalDate start = t.getStartDate() != null
                ? t.getStartDate()
                : (t.getDeadline() != null ? t.getDeadline() : today);

        // zuletzt abgeschlossene Instanz
        LocalDate lastCompleted = completedDates.stream()
                .max(LocalDate::compareTo)
                .orElse(null);

        // Basisdatum = nach letzter Completion, sonst Start
        LocalDate base = (lastCompleted != null)
                ? lastCompleted.plusDays(1)
                : start;

        // darf nicht in Vergangenheit liegen
        if (base.isBefore(today)) {
            base = today;
        }

        int interval = t.getRepeatInterval() != null ? t.getRepeatInterval() : 1;

        return switch (t.getRepeatFrequency()) {
            case DAILY -> base;
            case WEEKLY -> findNextWeekly(t, base);
            case MONTHLY -> base.plusMonths(interval)
                    .withDayOfMonth(start.getDayOfMonth());
            case YEARLY -> base.plusYears(interval)
                    .withDayOfYear(start.getDayOfYear());
            default -> base;
        };
    }

    private static LocalDate findNextWeekly(Task t, LocalDate base) {
        Set<String> allowed = new HashSet<>();
        if (t.getRepeatDays() != null) {
            for (String s : t.getRepeatDays().split(",")) {
                allowed.add(s.trim());
            }
        }

        LocalDate d = base;
        while (true) {
            String code = weekday(d);
            if (allowed.contains(code)) {
                return d;
            }
            d = d.plusDays(1);
        }
    }

    private static String weekday(LocalDate d) {
        return switch (d.getDayOfWeek()) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
        };
    }
}
