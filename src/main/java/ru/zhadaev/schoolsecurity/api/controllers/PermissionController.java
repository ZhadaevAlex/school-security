package ru.zhadaev.schoolsecurity.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zhadaev.schoolsecurity.api.dto.PermissionDto;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.service.PermissionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
@Validated
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping()
    public List<PermissionDto> findAll(Pageable pageable) {
        return permissionService.findAll(pageable);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnPostPut.class)
    public PermissionDto save(@RequestBody @Valid PermissionDto permissionDto) {
        return permissionService.save(permissionDto);
    }

    @GetMapping("/{id}")
    public PermissionDto findById(@PathVariable("id") String id) {
        return permissionService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") String id) {
        permissionService.deleteById(id);
    }

    @DeleteMapping()
    public void deleteAll() {
        permissionService.deleteAll();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PermissionDto updatePatch(@RequestBody PermissionDto permissionDto, @PathVariable("id") String id) {
        return permissionService.updatePatch(permissionDto, id);
    }
}
