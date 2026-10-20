package org.workswap.forum.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.forum.datasource.model.ForumComment;
import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.forum.datasource.model.ForumTopic;
import org.workswap.forum.enums.ForumActivityType;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;

public record ForumActivityItemDTO(
    String title,
    ShortUserDTO author,
    String link,
    String lang,
    LocalDateTime createdAt,
    String type
) {

    public static ForumActivityItemDTO ofTopic(ForumTopic topic) {
        User author = topic.getAuthor();

        return new ForumActivityItemDTO(
            topic.getTitle(),
            ShortUserDTO.ofUser(author),
            "/forum/topic/" + topic.getOpenId(),
            topic.getLanguage(),
            topic.getCreatedAt(),
            ForumActivityType.NEW_TOPIC.toString()
        );
    }

    public static ForumActivityItemDTO ofPost(ForumPost post) {

        ForumTopic topic = post.getTopic();
        User author = post.getAuthor();

        return new ForumActivityItemDTO(
            post.getContent(),
            ShortUserDTO.ofUser(author),
            "/forum/topic/" + topic.getOpenId(),
            topic.getLanguage(),
            post.getCreatedAt(),
            ForumActivityType.NEW_POST.toString()
        );
    }

    public static ForumActivityItemDTO ofComment(ForumComment comment) {

        ForumPost post = comment.getPost();
        ForumTopic topic = post.getTopic();
        User author = comment.getAuthor();

        return new ForumActivityItemDTO(
            comment.getContent(),
            ShortUserDTO.ofUser(author),
            "/forum/topic/" + topic.getOpenId(),
            topic.getLanguage(),
            comment.getCreatedAt(),
            ForumActivityType.NEW_COMMENT.toString()
        );
    }

    public static List<ForumActivityItemDTO> ofTopicsList(Collection<ForumTopic> topics) {
        return topics.stream().map(topic -> ForumActivityItemDTO.ofTopic(topic)).toList();
    }

    public static List<ForumActivityItemDTO> ofPostsList(Collection<ForumPost> posts) {
        return posts.stream().map(post -> ForumActivityItemDTO.ofPost(post)).toList();
    }

    public static List<ForumActivityItemDTO> ofCommentsList(Collection<ForumComment> comments) {
        return comments.stream().map(comment -> ForumActivityItemDTO.ofComment(comment)).toList();
    }
}
