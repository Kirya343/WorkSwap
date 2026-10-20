package org.workswap.task.dto;

import java.time.LocalDateTime;

import org.workswap.task.enums.TaskType;

public record TaskCreateDTO(
    String name,
    String description,
    TaskType type,
    LocalDateTime deadline
) {}
