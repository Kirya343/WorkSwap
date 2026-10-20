package org.workswap.permission.services;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.permission.dto.PermissionDTO;
import org.workswap.permission.dto.RoleDTO;
import org.workswap.permission.services.PermissionQueryService;

import org.workswap.permission.datasource.model.Permission;
import org.workswap.permission.datasource.model.Role;
import org.workswap.permission.datasource.repository.PermissionRepository;
import org.workswap.permission.datasource.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"server", "statistic"})
@RequiredArgsConstructor
public class PermissionQueryService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<RoleDTO> getAllRoleDtos() {
        List<Role> roles = roleRepository.findAll();

        return RoleDTO.ofList(roles);
    }

    public List<PermissionDTO> getAllPermissionDtos() {
        List<Permission> perms = permissionRepository.findAll();

        return PermissionDTO.ofList(perms);
    }

    public List<PermissionDTO> getPermissionDtosByRole(Long roleId) {
        List<Permission> perms = permissionRepository.findByRole(roleId);
        
        return PermissionDTO.ofList(perms);
    }
}
