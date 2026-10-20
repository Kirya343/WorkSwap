package org.workswap.sso.datasource.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.sso.datasource.model.User;
import org.workswap.sso.security.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findBySub(String ssoId);

    boolean existsByEmail(String email);
    boolean existsByName(String name);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);

    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<User> findAllByStatusOrderByCreatedAtDesc(Pageable pageable, UserStatus status);

    List<User> findByStatus(UserStatus status);

    @Modifying
    @Transactional
    @Query("""
        update User u
        set u.lastUsed = :time
        where u.id = :id
    """)
    void touchLastUsed(@Param("id") Long userId, @Param("time") LocalDateTime time);
}
