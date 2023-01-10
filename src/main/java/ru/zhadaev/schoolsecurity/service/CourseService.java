package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper mapper;

    public CourseDto save(CourseDto courseDto) {
        Course course = mapper.toEntity(courseDto);
        Course saved = courseRepository.save(course);
        UUID id = saved.getId();
        return mapper.toDto(saved);
    }

    public CourseDto replace(CourseDto courseDto, UUID id) {
        if (!existsById(id)) throw new NotFoundException("Group replace error. Group not found by id");
        Course course = mapper.toEntity(courseDto);
        course.setId(id);
        Course replaced = courseRepository.save(course);
        return mapper.toDto(replaced);
    }

    public CourseDto update(CourseDto courseDto, UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course update error. Course not found by id"));
        mapper.update(courseDto, course);
        return mapper.toDto(course);
    }

    public CourseDto findById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found by id"));
        return mapper.toDto(course);
    }

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
            throw new NotFoundException("Course delete error. Course not found by id");
        }
    }

    public void delete(Course course) {
        courseRepository.delete(course);
    }

    public void deleteAll() {
        courseRepository.deleteAll();
    }
}
