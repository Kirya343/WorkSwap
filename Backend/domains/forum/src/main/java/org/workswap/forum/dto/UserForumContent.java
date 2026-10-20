package org.workswap.forum.dto;

import java.util.List;

public record UserForumContent(
    List<ForumTopicDTO> topics, 
    List<ForumPostDTO> posts,
    List<ForumCommentDTO> comments
) {
}
