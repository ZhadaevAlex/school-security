package ru.zhadaev.schoolsecurity.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.zhadaev.schoolsecurity.dao.entities.Student;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student, UUID> {

    @EntityGraph(value = "student-entity-graph")
    @Query("select student from Student student left join student.courses course where course.id = ?1")
    List<Student> findStudentsByCourseId(UUID courseId, Pageable pageable);

    @Override
    @EntityGraph(value = "student-entity-graph")
    Page<Student> findAll(Pageable pageable);
}

