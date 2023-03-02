package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.PermissionDto;
import ru.zhadaev.schoolsecurity.api.mappers.PermissionMapper;
import ru.zhadaev.schoolsecurity.dao.repositories.PermissionRepository;
import ru.zhadaev.schoolsecurity.dao.entities.Permission;
import ru.zhadaev.schoolsecurity.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper mapper;

    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public PermissionDto save(PermissionDto permissionDto) {
        Permission permission = mapper.toEntity(permissionDto);
        Permission saved = permissionRepository.save(permission);
        return mapper.toDto(saved);
    }

    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public PermissionDto updatePatch(PermissionDto permissionDto, String id) {
        PermissionDto found = this.findById(id);
        Permission permission = mapper.toEntity(found);
        mapper.update(permissionDto, permission);
        permissionRepository.save(permission);
        return mapper.toDto(permission);
    }

    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public PermissionDto findById(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Permission not found by id = %s", id)));
        return mapper.toDto(permission);
    }

    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public List<PermissionDto> findAll(Pageable pageable) {
        List<Permission> permissions = permissionRepository.findAll(pageable).toList();
        return mapper.toDto(permissions);
    }

    public boolean existsById(String id) {
        return permissionRepository.existsById(id);
    }

    public long count() {
        return permissionRepository.count();
    }

    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public void deleteById(String id) {
        if (existsById(id)) {
            permissionRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Permission delete error. Permission not found by id = %s", id));
        }
    }

    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public void delete(Permission permission) {
        permissionRepository.delete(permission);
    }

    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public void deleteAll() {
        permissionRepository.deleteAll();
    }
}
