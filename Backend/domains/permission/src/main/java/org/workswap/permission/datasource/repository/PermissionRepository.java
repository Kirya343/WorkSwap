package org.workswap.permission.datasource.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.permission.datasource.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Permission findByName(String name);

    @Query("""
        SELECT r.permissions
        FROM Role r
        WHERE r.id = :roleId
        """)
    List<Permission> findByRole(@Param("roleId") Long roleId);

    List<Permission> findAllByIdIn(List<Long> ids);
    
    @Modifying
    @Transactional
    @Query("""
        UPDATE Permission p
        SET
            p.name = COALESCE(:name, p.name),
            p.comment = COALESCE(:comment, p.comment)
        WHERE p.id = :permissionId
    """)
    void updatePermission(
            @Param("permissionId") Long permissionId,
            @Param("name") String name,
            @Param("comment") String comment
    );
}
