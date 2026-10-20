package org.workswap.forum.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.workswap.category.dto.CategoryDTO;
import org.workswap.forum.datasource.model.ForumComment;
import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.forum.datasource.model.ForumTag;
import org.workswap.forum.datasource.model.ForumTopic;
import org.workswap.forum.datasource.repository.ForumCommentRepository;
import org.workswap.forum.datasource.repository.ForumPostRepository;
import org.workswap.forum.datasource.repository.ForumTagRepository;
import org.workswap.forum.datasource.repository.ForumTopicRepository;
import org.workswap.forum.dto.ForumActivityItemDTO;
import org.workswap.forum.dto.ForumCommentDTO;
import org.workswap.forum.dto.ForumPostDTO;
import org.workswap.forum.dto.ForumTopicDTO;
import org.workswap.forum.dto.UserForumContent;
import org.workswap.forum.services.ForumQueryService;
import org.workswap.shared.locale.LocalisationConfig.LanguageUtils;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"server"})
@RequiredArgsConstructor
public class ForumQueryServiceImpl implements ForumQueryService {
    
    private final ForumTopicRepository topicRepository;
    private final ForumPostRepository postRepository;
    private final ForumTagRepository tagRepository;
    private final ForumCommentRepository commentRepository;
    
    public ForumTopicDTO getTopic(String topicOpenId) {
        ForumTopic topic = topicRepository.findTopicWithPosts(topicOpenId);
        postRepository.fetchCommentsForPosts(topic.getPosts());
        return ForumTopicDTO.ofTopic(topic);
    }

    public List<ForumTopicDTO> getForumPage(
        String lang,
        boolean translationsFilter,
        int count
    ) {
        //простейшая защита от перегрузки, потому что количество будет контролироваться на фронте
        if (count > 30) count = 30;

        List<String> languages = new ArrayList<>();

        if (translationsFilter) {
            if (!languages.contains(lang)) {
                languages.add(lang);
            }
        } else {
            languages.addAll(LanguageUtils.SUPPORTED_LANGUAGES);
        }

        List<ForumTopic> topics = topicRepository.findByLanguagesWithAuthor(
            languages, 
            PageRequest.of(0, 20, Sort.by("createdAt").descending())
        );

        return ForumTopicDTO.ofList(topics);
    }

    public List<CategoryDTO> getForumtags() {
        List<ForumTag> tags = tagRepository.findAll();

        return CategoryDTO.ofList(tags);
    }

    public UserForumContent getUserForumContent(String userSub) {
        List<ForumTopic> topics = topicRepository.findByAuthorSub(userSub);
        List<ForumPost> posts = postRepository.findByAuthorSub(userSub);
        List<ForumComment> comments = commentRepository.findByAuthorSub(userSub);

        return new UserForumContent(
            ForumTopicDTO.ofList(topics),
            ForumPostDTO.ofList(posts),
            ForumCommentDTO.ofList(comments)
        );
    }

    public List<ForumActivityItemDTO> getForumActivity() {
        Pageable pageable = PageRequest.of(0, 15);
        List<ForumActivityItemDTO> activity = new ArrayList<>();

        List<ForumTopic> topics = topicRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
        List<ForumPost> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
        List<ForumComment> comments = commentRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();

        activity.addAll(ForumActivityItemDTO.ofTopicsList(topics));
        activity.addAll(ForumActivityItemDTO.ofPostsList(posts));
        activity.addAll(ForumActivityItemDTO.ofCommentsList(comments));
        
        return activity;
    }
}
