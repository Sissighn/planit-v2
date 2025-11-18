package com.setayesh.planit.core;

import java.time.LocalDate;
import java.util.*;

public class RecurrenceUtils {

    /**
     * Computes the NEXT upcoming occurrence of a recurring task.
     * Returns null if the recurrence series is finished.
     */
    public static LocalDate computeNextOccurrence(Task t, List<LocalDate> completedDates) {

        // ---------------------------------------------------------
        // NON-RECURRING: next occurrence is simply the deadline
        // ---------------------------------------------------------
        if (t.getRepeatFrequency() == RepeatFrequency.NONE) {
            return t.getDeadline();
        }

        LocalDate today = LocalDate.now();

        // ---------------------------------------------------------
        // Determine START date
        // ---------------------------------------------------------
        LocalDate start = Optional.ofNullable(t.getStartDate())
                .orElse(Optional.ofNullable(t.getDeadline())
                        .orElse(today));

        // ---------------------------------------------------------
        // Determine BASE date (after last completion OR start)
        // ---------------------------------------------------------
        LocalDate lastCompleted = completedDates.stream()
                .max(LocalDate::compareTo)
                .orElse(null);

        LocalDate base = (lastCompleted != null)
                ? lastCompleted.plusDays(1)
                : start;

        // Next occurrence should NEVER go backwards
        if (base.isBefore(today)) {
            base = today;
        }

        // ---------------------------------------------------------
        // Apply REPEAT-UNTIL (series end)
        // ---------------------------------------------------------
        if (t.getRepeatUntil() != null && base.isAfter(t.getRepeatUntil())) {
            return null;
        }

        int interval = Optional.ofNullable(t.getRepeatInterval()).orElse(1);

        // ---------------------------------------------------------
        // ROUTING TO SPECIFIC FREQUENCY
        // ---------------------------------------------------------
        return switch (t.getRepeatFrequency()) {

            case DAILY -> nextDaily(t, base, interval);

            case WEEKLY -> nextWeekly(t, base, interval);

            case MONTHLY -> nextMonthly(t, base, interval, start);

            case YEARLY -> nextYearly(t, base, interval, start);

            default -> null;
        };
    }

    // -----------------------------------------------------------------
    // DAILY
    // -----------------------------------------------------------------
    private static LocalDate nextDaily(Task t, LocalDate base, int interval) {
        LocalDate next = base;
        if (t.getRepeatUntil() != null && next.isAfter(t.getRepeatUntil())) {
            return null;
        }
        return next;
    }

    // -----------------------------------------------------------------
    // WEEKLY (supports multiple days)
    // -----------------------------------------------------------------
    private static LocalDate nextWeekly(Task t, LocalDate base, int interval) {

        // Parse allowed weekdays
        Set<String> allowed = new HashSet<>();
        if (t.getRepeatDays() != null) {
            for (String s : t.getRepeatDays().split(",")) {
                allowed.add(s.trim().toUpperCase());
            }
        }

        // If no days selected → treat like DAILY
        if (allowed.isEmpty()) {
            return nextDaily(t, base, interval);
        }

        LocalDate d = base;

        while (true) {
            String code = weekday(d);

            if (allowed.contains(code)) {
                if (t.getRepeatUntil() != null && d.isAfter(t.getRepeatUntil())) {
                    return null;
                }
                return d;
            }

            d = d.plusDays(1);
        }
    }

    // -----------------------------------------------------------------
    // MONTHLY (same day-of-month)
    // -----------------------------------------------------------------
    private static LocalDate nextMonthly(Task t, LocalDate base, int interval, LocalDate start) {

        // If base is still inside start month → return base
        if (base.getDayOfMonth() == start.getDayOfMonth()) {
            if (t.getRepeatUntil() != null && base.isAfter(t.getRepeatUntil())) {
                return null;
            }
            return base;
        }

        // Otherwise go to next month with interval
        LocalDate next = base.plusMonths(interval);

        // Try same day-of-month, fallback if invalid (e.g. Feb 30)
        int day = start.getDayOfMonth();
        int maxDay = next.lengthOfMonth();
        next = next.withDayOfMonth(Math.min(day, maxDay));

        if (t.getRepeatUntil() != null && next.isAfter(t.getRepeatUntil())) {
            return null;
        }

        return next;
    }

    // -----------------------------------------------------------------
    // YEARLY (same day-of-year)
    // -----------------------------------------------------------------
    private static LocalDate nextYearly(Task t, LocalDate base, int interval, LocalDate start) {

        if (base.getDayOfYear() == start.getDayOfYear()) {
            if (t.getRepeatUntil() != null && base.isAfter(t.getRepeatUntil())) {
                return null;
            }
            return base;
        }

        LocalDate next = base.plusYears(interval)
                .withDayOfYear(start.getDayOfYear());

        if (t.getRepeatUntil() != null && next.isAfter(t.getRepeatUntil())) {
            return null;
        }

        return next;
    }

    // -----------------------------------------------------------------
    // HELPER: Convert weekday to MON/TUE/WED...
    // -----------------------------------------------------------------
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
