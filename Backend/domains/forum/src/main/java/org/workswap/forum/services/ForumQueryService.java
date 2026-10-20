package org.workswap.forum.services;

import java.util.List;

import org.workswap.category.dto.CategoryDTO;
import org.workswap.forum.dto.ForumActivityItemDTO;
import org.workswap.forum.dto.ForumTopicDTO;
import org.workswap.forum.dto.UserForumContent;

public interface ForumQueryService {
    ForumTopicDTO getTopic(String topicOpenId);
    List<ForumTopicDTO> getForumPage(
        String lang,
        boolean translationsFilter,
        int count
    );
    List<CategoryDTO> getForumtags();
    UserForumContent getUserForumContent(String userSub);
    List<ForumActivityItemDTO> getForumActivity();
}
