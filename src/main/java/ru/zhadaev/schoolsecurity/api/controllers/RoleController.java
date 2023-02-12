package ru.zhadaev.schoolsecurity.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zhadaev.schoolsecurity.api.dto.RoleDto;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.enums.RoleName;
import ru.zhadaev.schoolsecurity.service.RoleService;

import javax.validation.Valid;
import java.util.List;

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
    public RoleDto findById(@PathVariable("id") RoleName id) {
        return roleService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") RoleName id) {
        roleService.deleteById(id);
    }

    @DeleteMapping()
    public void deleteAll() {
        roleService.deleteAll();
    }
}
