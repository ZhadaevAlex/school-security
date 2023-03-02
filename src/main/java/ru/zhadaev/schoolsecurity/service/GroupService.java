package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.GroupDto;
import ru.zhadaev.schoolsecurity.api.mappers.GroupMapper;
import ru.zhadaev.schoolsecurity.dao.entities.Group;
import ru.zhadaev.schoolsecurity.dao.repositories.GroupRepository;
import ru.zhadaev.schoolsecurity.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper mapper;

    @PreAuthorize("hasAuthority('GROUP_CREATE')")
    public GroupDto save(GroupDto groupDto) {
        Group group = mapper.toEntity(groupDto);
        Group saved = groupRepository.save(group);
        return mapper.toDto(saved);
    }

    @PreAuthorize("hasAuthority('GROUP_UPDATE')")
    public GroupDto updatePut(GroupDto groupDto, UUID id) {
        if (!this.existsById(id)) {
            throw new NotFoundException(String.format("Group replace error. Group not found by id = %s", id));
        }
        Group group = mapper.toEntity(groupDto);
        group.setId(id);
        Group replaced = groupRepository.save(group);
        return mapper.toDto(replaced);
    }

    @PreAuthorize("hasAuthority('GROUP_UPDATE')")
    public GroupDto updatePatch(GroupDto groupDto, UUID id) {
        GroupDto found = this.findById(id);
        Group group = mapper.toEntity(found);
        mapper.update(groupDto, group);
        groupRepository.save(group);
        return mapper.toDto(group);
    }

    @PreAuthorize("hasAuthority('GROUP_READ')")
    public GroupDto findById(UUID id) {
        Group group = groupRepository.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Group not found by id = %s", id)));
        return mapper.toDto(group);
    }

    @PreAuthorize("hasAuthority('GROUP_READ')")
    public List<GroupDto> findAll(Integer numberStudents, Pageable pageable) {
        List<Group> groups = (numberStudents == null) ?
                groupRepository.findAll(pageable).toList()
                : groupRepository.findByNumberStudents(numberStudents, pageable);
        return mapper.toDto(groups);
    }

    public boolean existsById(UUID id) {
        return groupRepository.existsById(id);
    }

    public long count() {
        return groupRepository.count();
    }

    @PreAuthorize("hasAuthority('GROUP_DELETE')")
    public void deleteById(UUID id) {
        if (existsById(id)) {
            groupRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Group delete error. Group not found by id = %s", id));
        }
    }

    @PreAuthorize("hasAuthority('GROUP_DELETE')")
    public void delete(Group group) {
        groupRepository.delete(group);
    }

    @PreAuthorize("hasAuthority('GROUP_DELETE')")
    public void deleteAll() {
        groupRepository.deleteAll();
    }
}
