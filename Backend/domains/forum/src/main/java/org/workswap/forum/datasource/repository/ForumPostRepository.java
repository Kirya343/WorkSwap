package org.workswap.forum.datasource.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.forum.datasource.model.ForumPost;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    Optional<ForumPost> findByOpenId(String openId);

    @Query("""
        SELECT DISTINCT p FROM ForumPost p
        LEFT JOIN FETCH p.comments c
        LEFT JOIN FETCH c.author
        WHERE p IN :posts
        """)
    List<ForumPost> fetchCommentsForPosts(@Param("posts") List<ForumPost> posts);

    List<ForumPost> findByAuthorSub(String authorSub);

    Page<ForumPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // можно удалить напрямую по openId и authorId без загрузки сущности
    void deleteByOpenIdAndAuthorSub(String openId, String authorSub);
}
