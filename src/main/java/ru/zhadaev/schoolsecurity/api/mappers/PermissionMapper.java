package ru.zhadaev.schoolsecurity.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zhadaev.schoolsecurity.api.dto.PermissionDto;
import ru.zhadaev.schoolsecurity.dao.entities.Permission;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {
    PermissionDto toDto(Permission permission);
    Permission toEntity(PermissionDto permissionDto);
    List<PermissionDto> toDto(List<Permission> permissions);
    void update(PermissionDto permissionDto, @MappingTarget Permission permission);
}