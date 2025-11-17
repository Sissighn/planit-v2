package com.setayesh.planit.api;

import com.setayesh.planit.core.Task;

public class TaskMapper {

    public static TaskDTO toDTO(Task t) {
        TaskDTO dto = new TaskDTO();
        dto.id = t.getId();
        dto.title = t.getTitle();
        dto.deadline = t.getDeadline();
        dto.priority = t.getPriority();
        dto.groupId = t.getGroupId();
        dto.done = t.isDone();
        dto.archived = t.isArchived();
        dto.time = t.getTime();
        dto.excludedDates = t.getExcludedDates();

        dto.repeatFrequency = t.getRepeatFrequency();
        dto.repeatDays = t.getRepeatDays();
        dto.repeatUntil = t.getRepeatUntil();
        dto.repeatInterval = t.getRepeatInterval();

        return dto;
    }

    public static void updateTaskFromDTO(Task t, TaskDTO dto) {
        if (dto.title != null)
            t.setTitle(dto.title);
        if (dto.deadline != null)
            t.setDeadline(dto.deadline);
        if (dto.priority != null)
            t.setPriority(dto.priority);
        if (dto.groupId != null)
            t.setGroupId(dto.groupId);

        t.setArchived(dto.archived);
        if (dto.done)
            t.markDone();
        else
            t.markUndone();

        // Recurrence fields
        t.setRepeatFrequency(dto.repeatFrequency);
        t.setRepeatDays(dto.repeatDays);
        t.setRepeatUntil(dto.repeatUntil);
        t.setRepeatInterval(dto.repeatInterval);

        if (dto.time != null)
            t.setTime(dto.time);

        if (dto.excludedDates != null)
            t.setExcludedDates(dto.excludedDates);

    }
}
