package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper mapper;

    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public StudentDto save(StudentDto studentDto) {
        Student student = mapper.toEntity(studentDto);
        Student saved = studentRepository.save(student);
        return mapper.toDto(saved);
    }

    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    public StudentDto updatePut(StudentDto studentDto, UUID id) {
        if (!existsById(id)) {
            throw new NotFoundException(String.format("Student replace error. Student not found by id = %s", id));
        }
        Student student = mapper.toEntity(studentDto);
        student.setId(id);
        Student replaced = studentRepository.save(student);
        return mapper.toDto(replaced);
    }

    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    public StudentDto updatePatch(StudentDto studentDto, UUID id) {
        StudentDto found = this.findById(id);
        Student student = mapper.toEntity(found);
        mapper.update(studentDto, student);
        studentRepository.save(student);
        return mapper.toDto(student);
    }

    @PreAuthorize("hasAuthority('STUDENT_READ')")
    public StudentDto findById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Student not found by id = %s", id)));
        return mapper.toDto(student);
    }

    @PreAuthorize("hasAuthority('STUDENT_READ')")
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

    @PreAuthorize("hasAuthority('STUDENT_DELETE')")
    public void deleteById(UUID id) {
        if (existsById(id)) {
            studentRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Student delete error. Student not found by id = %s", id));
        }
    }

    @PreAuthorize("hasAuthority('STUDENT_DELETE')")
    public void delete(Student student) {
        studentRepository.delete(student);
    }

    @PreAuthorize("hasAuthority('STUDENT_DELETE')")
    public void deleteAll() {
        studentRepository.deleteAll();
    }
}
