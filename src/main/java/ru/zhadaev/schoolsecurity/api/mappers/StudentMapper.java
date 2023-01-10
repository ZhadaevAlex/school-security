package ru.zhadaev.schoolsecurity.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zhadaev.schoolsecurity.api.dto.StudentDto;
import ru.zhadaev.schoolsecurity.dao.entities.Student;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {
    StudentDto toDto(Student student);
    Student toEntity(StudentDto studentDto);
    List<StudentDto> toDto(List<Student> students);
    void update(StudentDto studentDto, @MappingTarget Student student);
}
