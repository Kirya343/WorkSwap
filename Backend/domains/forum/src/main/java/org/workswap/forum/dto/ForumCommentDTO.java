package org.workswap.forum.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.forum.datasource.model.ForumComment;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;

public record ForumCommentDTO(
    Long id, 
    String content, 
    String postOpenId, 
    LocalDateTime createdAt,
    ShortUserDTO author
) {

    public static ForumCommentDTO ofComment(ForumComment comment) {
        User author = comment.getAuthor();

        return new ForumCommentDTO(
            comment.getId(),
            comment.getContent(),
            comment.getPost().getOpenId(),
            comment.getCreatedAt(),
            ShortUserDTO.ofUser(author)
        );
    }

    public static List<ForumCommentDTO> ofList(Collection<ForumComment> comments) {
        return comments.stream().map(comment -> ForumCommentDTO.ofComment(comment)).toList();
    }
}
