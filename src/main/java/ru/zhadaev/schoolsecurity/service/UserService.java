package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.UserDto;
import ru.zhadaev.schoolsecurity.api.errors.NotFoundException;
import ru.zhadaev.schoolsecurity.api.mappers.UserMapper;
import ru.zhadaev.schoolsecurity.dao.entities.User;
import ru.zhadaev.schoolsecurity.dao.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Secured("ROLE_SUPER_ADMIN")
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserDto save(UserDto userDto) {
        User user = mapper.toEntity(userDto);
        User saved = userRepository.save(user);
        UUID id = saved.getId();
        return mapper.toDto(saved);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER"})
    public UserDto replace(UserDto userDto, UUID id) {
        if (!existsById(id)) throw new NotFoundException("Group replace error. Group not found by id");
        User user = mapper.toEntity(userDto);
        user.setId(id);
        User replaced = userRepository.save(user);
        return mapper.toDto(replaced);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER"})
    public UserDto update(UserDto userDto, UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User update error. User not found by id"));
        mapper.update(userDto, user);
        return mapper.toDto(user);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER"})
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found by id"));
        return mapper.toDto(user);
    }

    @Secured({"ROLE_SUPER_ADMIN", "ROLE_MANAGER"})
    public List<UserDto> findAll(Pageable pageable) {
        List<User> users = userRepository.findAll(pageable).toList();
        return mapper.toDto(users);
    }

    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    public long count() {
        return userRepository.count();
    }

    public void deleteById(UUID id) {
        if (existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException("User delete error. User not found by id");
        }
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
