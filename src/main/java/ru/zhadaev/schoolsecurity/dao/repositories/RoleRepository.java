package ru.zhadaev.schoolsecurity.dao.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.zhadaev.schoolsecurity.dao.entities.Role;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
