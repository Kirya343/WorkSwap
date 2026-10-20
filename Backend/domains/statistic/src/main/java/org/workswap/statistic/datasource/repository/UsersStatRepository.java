package org.workswap.statistic.datasource.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.workswap.statistic.datasource.model.UsersStatSnapshot;

@Repository
public interface UsersStatRepository extends JpaRepository<UsersStatSnapshot, Long> {

    List<UsersStatSnapshot> findByTimestampAfter(LocalDateTime dateTime);

    UsersStatSnapshot findFirstByTimestampLessThanEqualOrderByTimestampDesc(
        LocalDateTime timestamp
    );

    UsersStatSnapshot findFirstByOrderByTimestampDesc();
}
