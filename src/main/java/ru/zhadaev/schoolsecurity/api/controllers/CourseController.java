package ru.zhadaev.schoolsecurity.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zhadaev.schoolsecurity.api.dto.CourseDto;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.service.CourseService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
@Validated
public class CourseController {
    private final CourseService courseService;

    @GetMapping()
    public List<CourseDto> findAll(Pageable pageable) {
        return courseService.findAll(pageable);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnPostPut.class)
    public CourseDto save(@RequestBody @Valid CourseDto courseDto) {
        return courseService.save(courseDto);
    }

    @GetMapping("/{id}")
    public CourseDto findById(@PathVariable("id") UUID id) {
        return courseService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") UUID id) {
        courseService.deleteById(id);
    }

    @DeleteMapping()
    public void deleteAll() {
        courseService.deleteAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Validated(Marker.OnPostPut.class)
    public CourseDto updatePut(@RequestBody @Valid CourseDto courseDto, @PathVariable UUID id) {
        return courseService.updatePut(courseDto, id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Validated(Marker.OnPatch.class)
    public CourseDto updatePatch(@RequestBody @Valid CourseDto courseDto, @PathVariable("id") UUID id) {
        return courseService.updatePatch(courseDto, id);
    }
}
