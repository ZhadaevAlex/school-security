package ru.zhadaev.schoolsecurity.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zhadaev.schoolsecurity.api.dto.UserDto;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnPostPut.class)
    public UserDto save(@RequestBody @Valid UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") UUID id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
    }

    @DeleteMapping()
    public void deleteAll() {
        userService.deleteAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Validated(Marker.OnPostPut.class)
    public UserDto replace(@RequestBody @Valid UserDto userDto, @PathVariable UUID id) {
        return userService.replace(userDto, id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Validated(Marker.OnPatch.class)
    public UserDto update(@RequestBody @Valid UserDto userDto, @PathVariable("id") UUID id) {
        return userService.update(userDto, id);
    }
}
