package org.workswap.permission.dto;

import java.util.Collection;
import java.util.List;

import org.workswap.permission.datasource.model.Role;

public record RoleDTO(
    Long id,
    String name,
    int level
) {
    public static RoleDTO ofRole(Role role) {
        return new RoleDTO(role.getId(), role.getName(), role.getLevel());
    }

    public static List<RoleDTO> ofList(Collection<Role> roles) {
        return roles.stream().map(role -> RoleDTO.ofRole(role)).toList();
    }
}