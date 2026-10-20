package org.workswap.forum.datasource.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.workswap.forum.datasource.model.ForumComment;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    Optional<ForumComment> findByOpenId(String openId);
    List<ForumComment> findByAuthorSub(String authorSub);

    Page<ForumComment> findAllByOrderByCreatedAtDesc(Pageable pageable);

    void deleteByIdAndAuthorSub(Long commentId, String authorSub);
}
