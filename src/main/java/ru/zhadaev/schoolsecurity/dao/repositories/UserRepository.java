package ru.zhadaev.schoolsecurity.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zhadaev.schoolsecurity.dao.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
