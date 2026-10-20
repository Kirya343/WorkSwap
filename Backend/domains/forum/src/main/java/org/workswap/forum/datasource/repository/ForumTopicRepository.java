package org.workswap.forum.datasource.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.forum.datasource.model.ForumTopic;

@Repository
public interface ForumTopicRepository extends JpaRepository<ForumTopic, Long> {
    Optional<ForumTopic> findByOpenId(String openId);

    @Query("""
        SELECT DISTINCT t FROM ForumTopic t
        LEFT JOIN FETCH t.author
        LEFT JOIN FETCH t.posts p
        LEFT JOIN FETCH p.author
        WHERE t.openId = :openId
        """)
    ForumTopic findTopicWithPosts(String openId);

    @Query("SELECT f FROM ForumTopic f JOIN FETCH f.author WHERE f.openId = :openId")
    ForumTopic findByOpenIdWithAuthor(String openId);

    @Query("""
        SELECT t FROM ForumTopic t
        LEFT JOIN FETCH t.author
        WHERE t.language IN :languages
        """)
    List<ForumTopic> findByLanguagesWithAuthor(
            @Param("languages") List<String> languages,
            Pageable pageable
    );

    List<ForumTopic> findByAuthorSub(String authorSub);

    Page<ForumTopic> findAllByOrderByCreatedAtDesc(Pageable pageable);

    void deleteByOpenIdAndAuthorSub(String openId, String userSub);
}
