package ru.zhadaev.schoolsecurity.dao.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.zhadaev.schoolsecurity.dao.entities.Course;

import java.util.UUID;

@Repository
public interface CourseRepository extends PagingAndSortingRepository<Course, UUID> {
}

