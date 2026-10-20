package org.workswap.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.workswap.permission.datasource.model.Permission;
import org.workswap.permission.datasource.model.Role;
import org.workswap.permission.datasource.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
@RequiredArgsConstructor
public class PermissionsService {

    private final RoleRepository roleRepository;

    @Cacheable(
        value = "user-permissions",
        key = "#userSub"
    )
    public Collection<GrantedAuthority> getUserPermissions(String userSub) {

        log.debug("get permissions for user {}", userSub);

        Set<Role> roles = roleRepository.findRolesWithPermissionsByUserSub(userSub);

        Set<GrantedAuthority> authorities = new HashSet<>();

        // permissions
        roles.stream()
            .filter(role -> role.getPermissions() != null)
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .map(SimpleGrantedAuthority::new)
            .forEach(authorities::add);

        // roles with ROLE_
        roles.stream()
            .map(Role::getName)
            .map(roleName -> "ROLE_" + roleName)
            .map(SimpleGrantedAuthority::new)
            .forEach(authorities::add);

        log.debug("found authorities {}", authorities);

        return authorities;
    }
}