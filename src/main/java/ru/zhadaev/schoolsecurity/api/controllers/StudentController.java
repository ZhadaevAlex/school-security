package ru.zhadaev.schoolsecurity.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zhadaev.schoolsecurity.api.dto.StudentDto;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.service.StudentService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
@Validated
public class StudentController {
    private final StudentService studentService;

    @GetMapping()
    public List<StudentDto> findAll(@RequestParam(name = "courseId", required = false)  UUID courseId,
                                    Pageable pageable) {
        return studentService.findAll(courseId, pageable);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnPostPut.class)
    public StudentDto save(@RequestBody @Valid StudentDto studentDto) {
        return studentService.save(studentDto);
    }

    @GetMapping("/{id}")
    public StudentDto findById(@PathVariable("id") UUID id) {
        return studentService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") UUID id) {
        studentService.deleteById(id);
    }

    @DeleteMapping()
    public void deleteAll() {
        studentService.deleteAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Validated(Marker.OnPostPut.class)
    public StudentDto replace(@RequestBody @Valid StudentDto studentDto, @PathVariable UUID id) {
        return studentService.replace(studentDto, id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Validated(Marker.OnPatch.class)
    public StudentDto update(@RequestBody @Valid StudentDto studentDto, @PathVariable("id") UUID id) {
        return studentService.update(studentDto, id);
    }
}