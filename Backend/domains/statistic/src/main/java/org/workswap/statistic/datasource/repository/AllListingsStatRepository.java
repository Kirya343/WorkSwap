package org.workswap.statistic.datasource.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.workswap.statistic.datasource.model.AllListingsStatSnapshot;

@Repository
public interface AllListingsStatRepository extends JpaRepository<AllListingsStatSnapshot, Long> {

    List<AllListingsStatSnapshot> findByTimestampAfter(LocalDateTime dateTime);

    AllListingsStatSnapshot findFirstByTimestampLessThanEqualOrderByTimestampDesc(
        LocalDateTime timestamp
    );

    AllListingsStatSnapshot findFirstByOrderByTimestampDesc();
}
