package ru.zhadaev.schoolsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.api.dto.UserDto;
import ru.zhadaev.schoolsecurity.api.mappers.UserMapper;
import ru.zhadaev.schoolsecurity.dao.entities.User;
import ru.zhadaev.schoolsecurity.dao.repositories.UserRepository;
import ru.zhadaev.schoolsecurity.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAuthority('USER_CREATE')")
    public UserDto save(UserDto userDto) {
        User user = mapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return mapper.toDto(saved);
    }

    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserDto updatePut(UserDto userDto, UUID id) {
        if (!this.existsById(id)) {
            throw new NotFoundException(String.format("User replace error. User not found by id = %s", id));
        }
        User user = mapper.toEntity(userDto);
        user.setId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User replaced = userRepository.save(user);
        return mapper.toDto(replaced);
    }

    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserDto updatePatch(UserDto userDto, UUID id) {
        UserDto found = this.findById(id);
        User user = mapper.toEntity(found);
        mapper.update(userDto, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return mapper.toDto(user);
    }

    @PreAuthorize("hasAuthority('USER_READ')")
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User not found by id = %s", id)));
        return mapper.toDto(user);
    }

    @PreAuthorize("hasAuthority('USER_READ')")
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

    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void deleteById(UUID id) {
        if (existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("User delete error. User not found by id = %s", id));
        }
    }

    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void delete(User user) {
        if (existsById(user.getId())) {
            userRepository.delete(user);
        } else {
            throw new NotFoundException("User delete error. User not found");
        }
    }

    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
