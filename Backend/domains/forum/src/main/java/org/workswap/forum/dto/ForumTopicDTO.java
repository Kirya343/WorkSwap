package org.workswap.forum.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.forum.datasource.model.ForumTag;
import org.workswap.forum.datasource.model.ForumTopic;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;

public record ForumTopicDTO(
    String openId,
    String title,
    String content,
    String tagName,
    String language,
    LocalDateTime createdAt,
    ShortUserDTO author,
    List<ForumPostRequest> posts,
    int postsCount
) {

    public static ForumTopicDTO ofTopic(ForumTopic topic) {

        ForumTag tag = topic.getTag();
        User author = topic.getAuthor();
        
        return new ForumTopicDTO(
            topic.getOpenId(),
            topic.getTitle(),
            topic.getContent(),
            tag != null ? tag.getName() : null,
            topic.getLanguage(),
            topic.getCreatedAt(),
            ShortUserDTO.ofUser(author),
            ForumPostRequest.ofList(topic.getPosts()),
            topic.getPosts().size()
        );
    }

    public static ForumTopicDTO ofTopicWithoutPosts(ForumTopic topic) {
        ForumTag tag = topic.getTag();
        User author = topic.getAuthor();

        return new ForumTopicDTO(
            topic.getOpenId(),
            topic.getTitle(),
            topic.getContent(),
            tag != null ? tag.getName() : null,
            topic.getLanguage(),
            topic.getCreatedAt(),
            ShortUserDTO.ofUser(author),
            null,
            topic.getPosts().size()
        );
    }

    public static List<ForumTopicDTO> ofList(Collection<ForumTopic> topics) {
        return topics.stream().map(topic -> ForumTopicDTO.ofTopic(topic)).toList();
    }
}
