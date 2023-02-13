package ru.zhadaev.schoolsecurity.dao.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.zhadaev.schoolsecurity.dao.entities.Role;
import ru.zhadaev.schoolsecurity.enums.RoleName;
import java.util.Optional;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, RoleName> {
    Optional<Role> findByName(RoleName name);
}
