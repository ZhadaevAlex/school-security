package ru.zhadaev.schoolsecurity.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zhadaev.schoolsecurity.api.dto.CourseDto;
import ru.zhadaev.schoolsecurity.dao.entities.Course;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {
    CourseDto toDto(Course course);
    Course toEntity(CourseDto courseDto);
    List<CourseDto> toDto(List<Course> courses);
    void update(CourseDto courseDto, @MappingTarget Course course);
}
