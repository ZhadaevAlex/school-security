package ru.zhadaev.schoolsecurity.dao.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.zhadaev.schoolsecurity.dao.entities.Group;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, UUID> {

    @Query(value = "select * from school.school.groups where " +
            "(select count(*) from school.school.students where " +
            " school.school.students.group_id = school.school.groups.group_id) < ?1",
    nativeQuery = true)
    List<Group> findGroupsByNumberStudents(long numberStudents, Pageable pageable);
}

