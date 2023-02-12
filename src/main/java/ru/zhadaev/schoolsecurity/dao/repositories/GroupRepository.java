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

    @Query("select g from Group g " +
            "left join Student s on g.id = s.group.id " +
            "group by g.id having count(s) < :numberStudents")
    List<Group> findByNumberStudents(long numberStudents, Pageable pageable);
}

