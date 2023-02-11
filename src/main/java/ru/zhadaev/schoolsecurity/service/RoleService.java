package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.RoleDto;
import ru.zhadaev.schoolsecurity.api.errors.NotFoundException;
import ru.zhadaev.schoolsecurity.api.mappers.RoleMapper;
import ru.zhadaev.schoolsecurity.dao.entities.Role;
import ru.zhadaev.schoolsecurity.dao.repositories.RoleRepository;
import ru.zhadaev.schoolsecurity.enums.RoleName;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Secured("ROLE_SUPER_ADMIN")
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper mapper;

    public RoleDto save(RoleDto roleDto) {
        Role role = mapper.toEntity(roleDto);
        Role saved = roleRepository.save(role);
        return mapper.toDto(saved);
    }

    public RoleDto findById(RoleName id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Role not found by id = %s", id)));
        return mapper.toDto(role);
    }

    public List<RoleDto> findAll(Pageable pageable) {
        List<Role> roles = roleRepository.findAll(pageable).toList();
        return mapper.toDto(roles);
    }

    public boolean existsById(RoleName id) {
        return roleRepository.existsById(id);
    }

    public long count() {
        return roleRepository.count();
    }

    public void deleteById(RoleName id) {
        if (existsById(id)) {
            roleRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Role delete error. Role not found by id = %s", id));
        }
    }

    public void delete(Role role) {
        roleRepository.delete(role);
    }

    public void deleteAll() {
        roleRepository.deleteAll();
    }
}
