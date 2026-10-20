package org.workswap.forum.services.impl;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.forum.datasource.model.ForumComment;
import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.forum.datasource.model.ForumTag;
import org.workswap.forum.datasource.model.ForumTopic;
import org.workswap.forum.datasource.repository.ForumCommentRepository;
import org.workswap.forum.datasource.repository.ForumPostRepository;
import org.workswap.forum.datasource.repository.ForumTagRepository;
import org.workswap.forum.datasource.repository.ForumTopicRepository;
import org.workswap.forum.dto.ForumTopicDTO;
import org.workswap.forum.services.ForumCommandService;
import org.workswap.shared.locale.LanguageMapper;
import org.workswap.shared.locale.LocalisationConfig.LanguageUtils;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.datasource.model.User;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@Profile({"server"})
@RequiredArgsConstructor
public class ForumCommandServiceImpl implements ForumCommandService {

    private final EntityManager entityManager;
    
    private final ForumTopicRepository topicRepository;
    private final ForumTagRepository tagRepository;
    private final ForumPostRepository postRepository;
    private final ForumCommentRepository commentRepository;
    
    public ForumTopic createTopic(UserAuthData authData, ForumTopicDTO topicDto) {
        if (topicDto.title() == null || topicDto.title().isBlank()) {
            throw new IllegalStateException("Unable to save empty topic");
        }

        LanguageDetector detector = LanguageDetectorBuilder.fromLanguages(LanguageUtils.SUPPORTED_LANGUAGES_LINGUA).build();
        String lang = LanguageMapper.toShortCode(detector.detectLanguageOf(topicDto.title()));

        ForumTag tag = tagRepository.findByName(topicDto.tagName());

        User authorProxy = entityManager.getReference(User.class, authData.sub());
        ForumTopic newTopic = new ForumTopic(authorProxy, tag, topicDto.title(), topicDto.content(), lang);

        return topicRepository.save(newTopic);
    }

    public ForumPost createPost(UserAuthData authData, String topicOpenId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("Unable to save empty post");
        }
        Optional<ForumTopic> topic = topicRepository.findByOpenId(topicOpenId);
        if (!topic.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No topics exist with this OpenId");
        }

        User authorProxy = entityManager.getReference(User.class, authData.sub());
        ForumPost post = new ForumPost(topic.get(), authorProxy, content);
        return postRepository.save(post);
    }

    public ForumComment createComment(UserAuthData authData, String postOpenId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("Unable to save empty comment");
        }
        Optional<ForumPost> post = postRepository.findByOpenId(postOpenId);
        if (!post.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No posts exist with this OpenId");
        }

        User authorProxy = entityManager.getReference(User.class, authData.sub());
        ForumComment comment = new ForumComment(post.get(), authorProxy, content);
        return commentRepository.save(comment);
    }

    public void deleteTopic(UserAuthData authData, String topicOpenId) {
        topicRepository.deleteByOpenIdAndAuthorSub(topicOpenId, authData.sub());
    }

    public void deletePost(UserAuthData authData, String postOpenId) {
        postRepository.deleteByOpenIdAndAuthorSub(postOpenId, authData.sub());
    }

    public void deleteComment(UserAuthData authData, Long commentId) {
        commentRepository.deleteByIdAndAuthorSub(commentId, authData.sub());
    }
}
