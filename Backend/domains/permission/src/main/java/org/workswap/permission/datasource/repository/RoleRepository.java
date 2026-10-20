package org.workswap.permission.datasource.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.permission.datasource.model.Permission;
import org.workswap.permission.datasource.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
    
    List<Role> findByPermissionsContaining(Permission permission);

    @Query("""
        select distinct r
        from User u
        join u.roles r
        join fetch r.permissions
        where u.sub = :userSub
    """)
    Set<Role> findRolesWithPermissionsByUserSub(@Param("userSub") String userSub);

    @Modifying
    @Transactional
    @Query(
        value = """
            insert into role_permissions (role_id, permissions_id)
            values (:roleId, :permissionId)
            """,
        nativeQuery = true
    )
    void addPermissionToRole(
            @Param("roleId") Long roleId,
            @Param("permissionId") Long permissionId
    );

    @Modifying
    @Transactional
    @Query(
        value = """
            delete from role_permissions
            where role_id = :roleId and permissions_id = :permissionId
            """,
        nativeQuery = true
    )
    void removePermissionFromRole(
            @Param("roleId") Long roleId,
            @Param("permissionId") Long permissionId
    );

}
