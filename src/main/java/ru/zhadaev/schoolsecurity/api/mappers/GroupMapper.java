package ru.zhadaev.schoolsecurity.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zhadaev.schoolsecurity.api.dto.GroupDto;
import ru.zhadaev.schoolsecurity.dao.entities.Group;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GroupMapper {
    GroupDto toDto(Group group);
    Group toEntity(GroupDto groupDto);
    List<GroupDto> toDto(List<Group> groups);
    void update(GroupDto groupDto, @MappingTarget Group group);
}
