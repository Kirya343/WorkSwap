package org.workswap.forum.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;

public record ForumPostRequest(
    String openId,
    String content,
    LocalDateTime createdAt,
    ShortUserDTO author,
    List<ForumCommentDTO> comments
) {

    public static ForumPostRequest ofPost(ForumPost post) {
        User author = post.getAuthor();

        return new ForumPostRequest(
            post.getOpenId(), 
            post.getContent(), 
            post.getCreatedAt(),
            ShortUserDTO.ofUser(author),
            ForumCommentDTO.ofList(post.getComments())
        );
    }

    public static List<ForumPostRequest> ofList(Collection<ForumPost> posts) {
        return posts.stream().map(post -> ForumPostRequest.ofPost(post)).toList();
    }
}