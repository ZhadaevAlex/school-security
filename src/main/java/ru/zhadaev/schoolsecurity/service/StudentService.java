package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.StudentDto;
import ru.zhadaev.schoolsecurity.api.errors.NotFoundException;
import ru.zhadaev.schoolsecurity.api.mappers.StudentMapper;
import ru.zhadaev.schoolsecurity.dao.entities.Student;
import ru.zhadaev.schoolsecurity.dao.repositories.StudentRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN"})
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper mapper;

    public StudentDto save(StudentDto studentDto) {
        Student student = mapper.toEntity(studentDto);
        Student saved = studentRepository.save(student);
        return mapper.toDto(saved);
    }

    public StudentDto replace(StudentDto studentDto, UUID id) {
        if (!existsById(id)) {
            throw new NotFoundException(String.format("Student replace error. Student not found by id = %s", id));
        }
        Student student = mapper.toEntity(studentDto);
        student.setId(id);
        Student replaced = studentRepository.save(student);
        return mapper.toDto(replaced);
    }

    public StudentDto update(StudentDto studentDto, UUID id) {
        StudentDto found = this.findById(id);
        Student student = mapper.toEntity(found);
        mapper.update(studentDto, student);
        studentRepository.save(student);
        return mapper.toDto(student);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_USER"})
    public StudentDto findById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Student not found by id = %s", id)));
        return mapper.toDto(student);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_USER"})
    public List<StudentDto> findAll(UUID courseId, Pageable pageable) {
        List<Student> students = (courseId == null) ?
                studentRepository.findAll(pageable).toList()
                : studentRepository.findByCourseId(courseId, pageable);
        return mapper.toDto(students);
    }

    public boolean existsById(UUID id) {
        return studentRepository.existsById(id);
    }

    public long count() {
        return studentRepository.count();
    }

    public void deleteById(UUID id) {
        if (existsById(id)) {
            studentRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Student delete error. Student not found by id = %s", id));
        }
    }

    public void delete(Student student) {
        studentRepository.delete(student);
    }

    public void deleteAll() {
        studentRepository.deleteAll();
    }
}
