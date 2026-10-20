package org.workswap.permission.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.permission.services.PermissionCommandSevice;
import org.workswap.permission.datasource.model.Permission;
import org.workswap.permission.datasource.model.Role;
import org.workswap.permission.datasource.repository.PermissionRepository;
import org.workswap.permission.datasource.repository.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class PermissionCommandSevice {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void updateRolePermission(Long roleId, Long permissionId, boolean enabled) {

        if (enabled) {
            roleRepository.addPermissionToRole(roleId, permissionId);
        } else {
            roleRepository.removePermissionFromRole(roleId, permissionId);
        }
    }

    public Role createRole(String roleName) {
        Role role = new Role(roleName, 0);
        return roleRepository.save(role);
    }

    public Permission createPermisson(String permissionName) {
        Permission perm = new Permission(permissionName);
        return permissionRepository.save(perm);
    }
}
