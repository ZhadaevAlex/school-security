package ru.zhadaev.schoolsecurity.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zhadaev.schoolsecurity.api.dto.RoleDto;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.service.RoleService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
@Validated
public class RoleController {
    private final RoleService roleService;

    @GetMapping()
    public List<RoleDto> findAll(Pageable pageable) {
        return roleService.findAll(pageable);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnPostPut.class)
    public RoleDto save(@RequestBody @Valid RoleDto roleDto) {
        return roleService.save(roleDto);
    }

    @GetMapping("/{id}")
    public RoleDto findById(@PathVariable("id") UUID id) {
        return roleService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") UUID id) {
        roleService.deleteById(id);
    }

    @DeleteMapping()
    public void deleteAll() {
        roleService.deleteAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Validated(Marker.OnPostPut.class)
    public RoleDto replace(@RequestBody @Valid RoleDto roleDto, @PathVariable UUID id) {
        return roleService.replace(roleDto, id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Validated(Marker.OnPatch.class)
    public RoleDto update(@RequestBody @Valid RoleDto roleDto, @PathVariable("id") UUID id) {
        return roleService.update(roleDto, id);
    }
}
