package org.workswap.statistic.datasource.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workswap.statistic.datasource.model.ListingView;

public interface ListingViewRepository extends JpaRepository<ListingView, Long>{
    
    ListingView findByUserIdAndListingId(Long userId, Long listingId);

    boolean existsByUserIdAndListingId(Long userId, Long listingId);

    int countByTemporary(boolean temporary);
    int countByTemporaryAndCreatedAtAfter(boolean temporary, LocalDateTime date);
}
