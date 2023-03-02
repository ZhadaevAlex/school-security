package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.CourseDto;
import ru.zhadaev.schoolsecurity.api.mappers.CourseMapper;
import ru.zhadaev.schoolsecurity.dao.entities.Course;
import ru.zhadaev.schoolsecurity.dao.repositories.CourseRepository;
import ru.zhadaev.schoolsecurity.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper mapper;

    @PreAuthorize("hasAuthority('COURSE_CREATE')")
    public CourseDto save(CourseDto courseDto) {
        Course course = mapper.toEntity(courseDto);
        Course saved = courseRepository.save(course);
        return mapper.toDto(saved);
    }

    @PreAuthorize("hasAuthority('COURSE_UPDATE')")
    public CourseDto updatePut(CourseDto courseDto, UUID id) {
        if (!this.existsById(id)) {
            throw new NotFoundException(String.format("Course replace error. Course not found by id = %s", id));
        }
        Course course = mapper.toEntity(courseDto);
        course.setId(id);
        Course replaced = courseRepository.save(course);
        return mapper.toDto(replaced);
    }

    @PreAuthorize("hasAuthority('COURSE_UPDATE')")
    public CourseDto updatePatch(CourseDto courseDto, UUID id) {
        CourseDto found = this.findById(id);
        Course course = mapper.toEntity(found);
        mapper.update(courseDto, course);
        courseRepository.save(course);
        return mapper.toDto(course);
    }

    @PreAuthorize("hasAuthority('COURSE_READ')")
    public CourseDto findById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Course not found by id = %s", id)));
        return mapper.toDto(course);
    }

    @PreAuthorize("hasAuthority('COURSE_READ')")
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

    @PreAuthorize("hasAuthority('COURSE_DELETE')")
    public void deleteById(UUID id) {
        if (existsById(id)) {
            courseRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Course delete error. Course not found by id = %s", id));
        }
    }

    @PreAuthorize("hasAuthority('COURSE_DELETE')")
    public void delete(Course course) {
        if (existsById(course.getId())) {
            courseRepository.delete(course);
        } else {
            throw new NotFoundException("Course delete error. Course not found");
        }
    }

    @PreAuthorize("hasAuthority('COURSE_DELETE')")
    public void deleteAll() {
        courseRepository.deleteAll();
    }
}
