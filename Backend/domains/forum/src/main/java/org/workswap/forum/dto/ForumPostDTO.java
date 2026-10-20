package org.workswap.forum.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;

public record ForumPostDTO(
    String topicOpenId,
    String openId,
    String content,
    LocalDateTime createdAt,
    ShortUserDTO author
) {

    public static ForumPostDTO ofPost(ForumPost post) {
        User author = post.getAuthor();

        return new ForumPostDTO(
            null,
            post.getOpenId(),
            post.getContent(), 
            post.getCreatedAt(),
            ShortUserDTO.ofUser(author)
        );
    }

    public static List<ForumPostDTO> ofList(Collection<ForumPost> posts) {
        return posts.stream().map(post -> ForumPostDTO.ofPost(post)).toList();
    }
}
