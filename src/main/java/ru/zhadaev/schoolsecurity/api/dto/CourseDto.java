package ru.zhadaev.schoolsecurity.api.dto;

import lombok.Data;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.api.validation.PatchValidation;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class CourseDto {
    private UUID id;

    @PatchValidation(groups = Marker.OnPatch.class,
            message = "The course name must contain at least one non-whitespace character. Can be null")
    @NotBlank(groups = Marker.OnPostPut.class,
            message = "The course name must not be null and must contain at least one non-whitespace character")
    private String name;

    private String description;
}
