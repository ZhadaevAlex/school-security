package ru.zhadaev.schoolsecurity.dao.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.zhadaev.schoolsecurity.dao.entities.Permission;
import java.util.Optional;

@Repository
public interface PermissionRepository extends PagingAndSortingRepository<Permission, String> {
    Optional<Permission> findByName(String name);
}
