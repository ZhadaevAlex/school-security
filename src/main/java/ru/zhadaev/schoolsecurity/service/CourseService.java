package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.CourseDto;
import ru.zhadaev.schoolsecurity.api.errors.NotFoundException;
import ru.zhadaev.schoolsecurity.api.mappers.CourseMapper;
import ru.zhadaev.schoolsecurity.dao.entities.Course;
import ru.zhadaev.schoolsecurity.dao.repositories.CourseRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_TEACHER"})
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper mapper;

    public CourseDto save(CourseDto courseDto) {
        Course course = mapper.toEntity(courseDto);
        Course saved = courseRepository.save(course);
        return mapper.toDto(saved);
    }

    public CourseDto updatePut(CourseDto courseDto, UUID id) {
        if (!this.existsById(id)) {
            throw new NotFoundException(String.format("Course replace error. Course not found by id = %s", id));
        }
        Course course = mapper.toEntity(courseDto);
        course.setId(id);
        Course replaced = courseRepository.save(course);
        return mapper.toDto(replaced);
    }

    public CourseDto updatePatch(CourseDto courseDto, UUID id) {
        CourseDto found = this.findById(id);
        Course course = mapper.toEntity(found);
        mapper.update(courseDto, course);
        courseRepository.save(course);
        return mapper.toDto(course);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_USER"})
    public CourseDto findById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Course not found by id = %s", id)));
        return mapper.toDto(course);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_USER"})
    public List<CourseDto> findAll(Pageable pageable) {
        List<Course> courses = courseRepository.findAll(pageable).toList();
        return mapper.toDto(courses);
    }

    public boolean existsById(UUID id) {
        return courseRepository.existsById(id);
    }

    public long count() {
        return courseRepository.count();
    }

    public void deleteById(UUID id) {
        if (existsById(id)) {
            courseRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Course delete error. Course not found by id = %s", id));
        }
    }

    public void delete(Course course) {
        courseRepository.delete(course);
    }

    public void deleteAll() {
        courseRepository.deleteAll();
    }
}
