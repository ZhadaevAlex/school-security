package ru.zhadaev.schoolsecurity.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zhadaev.schoolsecurity.api.dto.UserDto;
import ru.zhadaev.schoolsecurity.dao.entities.User;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
    List<UserDto> toDto(List<User> users);
    void update(UserDto userDto, @MappingTarget User user);
}
