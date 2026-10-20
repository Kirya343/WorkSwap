package org.workswap.forum.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.category.dto.CategoryDTO;
import org.workswap.forum.dto.ForumActivityItemDTO;
import org.workswap.forum.dto.ForumCommentDTO;
import org.workswap.forum.dto.ForumPostDTO;
import org.workswap.forum.dto.ForumTopicDTO;
import org.workswap.forum.services.ForumCommandService;
import org.workswap.forum.services.ForumQueryService;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumCommandService forumCommandService;
    private final ForumQueryService forumQueryService;

    @GetMapping("/recent-topics")
    @PublicEndpoint
    public List<ForumTopicDTO> getForumPage(
        @RequestParam int count,
        @RequestParam String locale,
        @RequestParam boolean translationsFilter
    ) {
        return forumQueryService.getForumPage(locale, translationsFilter, count);
    }

    @GetMapping("/topic")
    @PublicEndpoint
    public ForumTopicDTO getTopic(
        @RequestParam String topicOpenId
    ) {
        return forumQueryService.getTopic(topicOpenId);
    }

    
    @PostMapping("/topic")
    @RequiredPermission("CREATE_FORUM_TOPIC")
    public String createTopic(
        @RequestBody ForumTopicDTO topicDTO,
        @AuthUser UserAuthData authData
    ) {
        return forumCommandService.createTopic(authData, topicDTO).getOpenId();
    }

    @PostMapping("/post")
    @RequiredPermission("CREATE_FORUM_POST")
    public String createPost(
        @RequestBody ForumPostDTO postDTO,
        @AuthUser UserAuthData authData
    ) {
        return forumCommandService.createPost(authData, postDTO.topicOpenId(), postDTO.content()).getOpenId();
    }

    @PostMapping("/comment")
    @RequiredPermission("CREATE_FORUM_COMMENT")
    public Long createComment(
        @RequestBody ForumCommentDTO ForumCommentDTO,
        @AuthUser UserAuthData authData
    ) {
        return forumCommandService.createComment(authData, ForumCommentDTO.postOpenId(), ForumCommentDTO.content()).getId();
    }

    @DeleteMapping("/topic")
    @RequiredPermission("DELETE_FORUM_TOPIC")
    public void deleteTopic(
        @RequestParam String topicOpenId,
        @AuthUser UserAuthData authData
    ) {
        forumCommandService.deleteTopic(authData, topicOpenId);
    }

    @DeleteMapping("/post")
    @RequiredPermission("DELETE_FORUM_POST")
    public void deletePost(
        @RequestParam String postOpenId,
        @AuthUser UserAuthData authData
    ) {
        forumCommandService.deletePost(authData, postOpenId);
    }

    @DeleteMapping("/comment")
    @RequiredPermission("DELETE_FORUM_COMMENT")
    public void deleteComment(
        @RequestParam Long commentId,
        @AuthUser UserAuthData authData
    ) {
        forumCommandService.deleteComment(authData, commentId);
    }

    @GetMapping("/tags")
    @PublicEndpoint
    public List<CategoryDTO> getTopic() {
        return forumQueryService.getForumtags();
    }

    @GetMapping("/activity")
    @PublicEndpoint
    public List<ForumActivityItemDTO> getForumActivity() {
        return forumQueryService.getForumActivity();
    }
}
