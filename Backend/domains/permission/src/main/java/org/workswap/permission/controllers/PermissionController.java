package org.workswap.permission.controllers;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.permission.datasource.repository.PermissionRepository;
import org.workswap.permission.dto.PermissionDTO;
import org.workswap.permission.dto.RoleDTO;
import org.workswap.permission.services.PermissionCommandSevice;
import org.workswap.permission.services.PermissionQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionQueryService permissionQueryService;
    private final PermissionCommandSevice permissionCommandSevice;
    private final PermissionRepository permissionRepository;

    @GetMapping
    @RequiredPermission("GET_ALL_PERMISSIONS")
    public List<PermissionDTO> getPermissions() {
        return permissionQueryService.getAllPermissionDtos();
    }

    @GetMapping("/roles")
    @RequiredPermission("GET_ALL_ROLES")
    public List<RoleDTO> getRoles() {
        return permissionQueryService.getAllRoleDtos();
    }

    @GetMapping("/{roleId}/get")
    @RequiredPermission("GET_PERMISSIONS_BY_ROLE")
    public List<PermissionDTO> getPermissionsByRole(@PathVariable Long roleId) {
        return permissionQueryService.getPermissionDtosByRole(roleId);
    }

    @PutMapping("/{roleId}/role")
    @RequiredPermission("MANAGE_PERMISSIONS")
    public void savePermissionsForRole(
        @PathVariable Long roleId,
        @RequestParam Long permissionId,
        @RequestParam boolean enabled
    ) {
        permissionCommandSevice.updateRolePermission(roleId, permissionId, enabled);
    }

    @PostMapping("/role")
    @RequiredPermission("CREATE_ROLE")
    public Long createRole(@RequestParam String roleName) {
        return permissionCommandSevice.createRole(roleName).getId();
    }

    @PostMapping
    @RequiredPermission("CREATE_PERMISSION")
    public Long createPermission(@RequestParam String permissionName) {
        return permissionCommandSevice.createPermisson(permissionName).getId();
    }

    @PatchMapping("/{permissionId}")
    @RequiredPermission("UPDATE_PERMISSION")
    public void updatePermission(
        @PathVariable Long permissionId,
        @RequestParam(required = false) String name, 
        @RequestParam(required = false) String comment
    ) {
        permissionRepository.updatePermission(permissionId, name, comment);
    }
}