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

import java.util.List;
import java.util.UUID;

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
        UUID id = saved.getId();
        return mapper.toDto(saved);
    }

    public RoleDto replace(RoleDto roleDto, UUID id) {
        if (!existsById(id)) throw new NotFoundException("Group replace error. Group not found by id");
        Role role = mapper.toEntity(roleDto);
        role.setId(id);
        Role replaced = roleRepository.save(role);
        return mapper.toDto(replaced);
    }

    public RoleDto update(RoleDto roleDto, UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role update error. Role not found by id"));
        mapper.update(roleDto, role);
        return mapper.toDto(role);
    }

    public RoleDto findById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found by id"));
        return mapper.toDto(role);
    }

    public List<RoleDto> findAll(Pageable pageable) {
        List<Role> roles = roleRepository.findAll(pageable).toList();
        return mapper.toDto(roles);
    }

    public boolean existsById(UUID id) {
        return roleRepository.existsById(id);
    }

    public long count() {
        return roleRepository.count();
    }

    public void deleteById(UUID id) {
        if (existsById(id)) {
            roleRepository.deleteById(id);
        } else {
            throw new NotFoundException("Role delete error. Role not found by id");
        }
    }

    public void delete(Role role) {
        roleRepository.delete(role);
    }

    public void deleteAll() {
        roleRepository.deleteAll();
    }
}
