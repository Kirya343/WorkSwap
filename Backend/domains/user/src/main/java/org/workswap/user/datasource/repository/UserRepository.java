package org.workswap.user.datasource.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.permission.datasource.model.Role;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.user.datasource.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findBySub(String sub);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);

    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<User> findAllByStatusOrderByCreatedAtDesc(Pageable pageable, UserStatus type);

    List<User> findByRolesContaining(Role role);
    List<User> findByRoles_Name(String roleName);
    List<User> findByRoles_NameIn(List<String> roleNames);
    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.settings WHERE u.sub = :userSub")
    User findBySubWithSettings(@Param("userSub") String userSub);

    @Query("SELECT u.languages FROM User u WHERE u.sub = :userSub")
    List<String> findLanguagesByUserSub(@Param("userSub") String userSub);

    @Query("""
        select distinct u
        from User u
        left join fetch u.languages
        left join fetch u.settings
        left join fetch u.roles r
        left join fetch r.permissions
        left join fetch u.location loc
        left join fetch loc.country
        where u.sub = :userSub
    """)
    Optional<User> getFullUser(@Param("userSub") String userSub);

    @Modifying
    @Transactional
    @Query("""
        update User u
        set u.lastUsed = :time
        where u.id = :id
    """)
    void touchLastUsed(@Param("id") Long userId, @Param("time") LocalDateTime time);

    @Modifying
    @Transactional
    @Query(
        value = """
            insert into user_roles (user_id, roles_id)
            values (:userId, :roleId)
            """,
        nativeQuery = true
    )
    void addRoleToUser(
            @Param("userId") Long userId,
            @Param("roleId") Long roleId
    );

    @Modifying
    @Transactional
    @Query(
        value = """
            delete from user_roles
            where user_id = :userId and roles_id = :roleId
            """,
        nativeQuery = true
    )
    void removeRoleFromUser(
            @Param("userId") Long userId,
            @Param("roleId") Long roleId
    );

    @Query("""
        SELECT u.id
        FROM User u
        WHERE u.status <> 'TEMP'
    """)
    Page<Long> findIds(Pageable pageable);

    @Query("""
        SELECT u
        FROM User u
        LEFT JOIN FETCH u.roles
        LEFT JOIN FETCH u.settings
        WHERE u.id IN :ids
    """)
    List<User> findWithRelationsByIds(List<Long> ids);
}
