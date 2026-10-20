package org.workswap.permission.dto;

import java.util.Collection;
import java.util.List;

import org.workswap.permission.datasource.model.Permission;

public record PermissionDTO(
    Long id,
    String name,
    String comment
) {
    public static PermissionDTO ofPermission(Permission perm) {
        return new PermissionDTO(perm.getId(), perm.getName(), perm.getComment());
    }

    public static List<PermissionDTO> ofList(Collection<Permission> perms) {
        return perms.stream().map(role -> PermissionDTO.ofPermission(role)).toList();
    }
}