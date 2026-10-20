package org.workswap.forum.services;

import org.workswap.forum.datasource.model.ForumComment;
import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.forum.datasource.model.ForumTopic;
import org.workswap.forum.dto.ForumTopicDTO;
import org.workswap.sso.security.dto.UserAuthData;

public interface ForumCommandService {
    ForumTopic createTopic(UserAuthData authData, ForumTopicDTO topicDto);
    ForumPost createPost(UserAuthData authData, String topicOpenId, String content);
    ForumComment createComment(UserAuthData authData, String postOpenId, String content);

    void deleteTopic(UserAuthData authData, String topicOpenId);
    void deletePost(UserAuthData authData, String postOpenId);
    void deleteComment(UserAuthData authData, Long commentId);
}
