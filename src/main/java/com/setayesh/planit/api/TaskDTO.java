package com.setayesh.planit.api;

import com.setayesh.planit.core.Priority;
import com.setayesh.planit.core.RepeatFrequency;

import java.time.LocalDate;
import java.util.UUID;

public class TaskDTO {

    public UUID id;
    public String title;
    public LocalDate deadline;
    public Priority priority;
    public Long groupId;
    public boolean done;
    public boolean archived;
    public String time;
    public String excludedDates;

    // Recurrence
    public RepeatFrequency repeatFrequency;
    public String repeatDays;
    public LocalDate repeatUntil;
    public Integer repeatInterval;

    public TaskDTO() {
    }
}
