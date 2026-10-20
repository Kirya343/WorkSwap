package org.workswap.task.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.task.datasource.model.TaskComment;
import org.workswap.user.dto.ShortUserDTO;

public record TaskCommentDTO(
    Long id,
    Long taskId,
    String content,
    LocalDateTime createdAt,
    ShortUserDTO author
) {
    public static TaskCommentDTO ofComment(TaskComment comment) {

        return new TaskCommentDTO(
            comment.getId(),
            comment.getTask().getId(),
            comment.getContent(),
            comment.getCreatedAt(),
            ShortUserDTO.ofUser(comment.getAuthor())
        );
    }

    public static List<TaskCommentDTO> ofList(Collection<TaskComment> comments) {
        return comments.stream().map(comment -> TaskCommentDTO.ofComment(comment)).toList();
    }
}
