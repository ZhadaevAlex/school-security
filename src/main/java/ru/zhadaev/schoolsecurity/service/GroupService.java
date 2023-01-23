package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.GroupDto;
import ru.zhadaev.schoolsecurity.api.errors.NotFoundException;
import ru.zhadaev.schoolsecurity.api.mappers.GroupMapper;
import ru.zhadaev.schoolsecurity.dao.entities.Group;
import ru.zhadaev.schoolsecurity.dao.repositories.GroupRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_TEACHER"})
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper mapper;

    public GroupDto save(GroupDto groupDto) {
        Group group = mapper.toEntity(groupDto);
        Group saved = groupRepository.save(group);
        UUID id = saved.getId();
        return mapper.toDto(saved);
    }

    public GroupDto replace(GroupDto groupDto, UUID id) {
        if (!existsById(id)) throw new NotFoundException("Group replace error. Group not found by id");
        Group group = mapper.toEntity(groupDto);
        group.setId(id);
        Group replaced = groupRepository.save(group);
        return mapper.toDto(replaced);
    }

    public GroupDto update(GroupDto groupDto, UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group update error. Group not found by id"));
        mapper.update(groupDto, group);
        return mapper.toDto(group);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_USER"})
    public GroupDto findById(UUID id) {
        Group group = groupRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Group not found by id"));
        return mapper.toDto(group);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_USER"})
    public List<GroupDto> findAll(Integer numberStudents, Pageable pageable) {
        List<Group> groups = (numberStudents == null) ?
                groupRepository.findAll(pageable).toList()
                : groupRepository.findGroupsByNumberStudents(numberStudents, pageable);
        return mapper.toDto(groups);
    }

    public boolean existsById(UUID id) {
        return groupRepository.existsById(id);
    }

    public long count() {
        return groupRepository.count();
    }

    public void deleteById(UUID id) {
        if (existsById(id)) {
            groupRepository.deleteById(id);
        } else {
            throw new NotFoundException("Group delete error. Group not found by id");
        }
    }

    public void delete(Group group) {
        groupRepository.delete(group);
    }

    public void deleteAll() {
        groupRepository.deleteAll();
    }
}
