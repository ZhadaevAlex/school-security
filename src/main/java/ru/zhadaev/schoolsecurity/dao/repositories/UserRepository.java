package ru.zhadaev.schoolsecurity.dao.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.zhadaev.schoolsecurity.dao.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<User, UUID> {
    Optional<User> findByLogin(String login);
}
