package org.workswap.review.datasource.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.workswap.review.datasource.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Пример кастомного метода:
    @EntityGraph(attributePaths = "author")
    List<Review> findByListingIdOrderByCreatedAtDesc(Long listingId);

    @EntityGraph(attributePaths = "author")
    List<Review> findByProfileSubOrderByCreatedAtDesc(String profileSub);

    @EntityGraph(attributePaths = "author")
    List<Review> findByAuthorSub(String authorSub);

    boolean existsByAuthorSubAndListingId(String authorSub, Long listingId);
    boolean existsByAuthorSubAndProfileSub(String authorSub, String profileDub);

    void deleteAllByListingId(Long listingId);
    void deleteAllByAuthorId(Long userId);
}