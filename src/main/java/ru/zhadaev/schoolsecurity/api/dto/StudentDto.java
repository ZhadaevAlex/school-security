package ru.zhadaev.schoolsecurity.api.dto;

import lombok.Data;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.api.validation.PatchValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Data
public class StudentDto {
    private UUID id;

    @PatchValidation(groups = Marker.OnPatch.class,
            message = "The student's first name must contain at least one non-whitespace character. Can be null")
    @NotBlank(groups = Marker.OnPostPut.class,
            message = "The student's first name must not be null and must contain at least one non-whitespace character")
    @Size(min = 2, message = "The student's first name must consist of at least two characters")
    private String firstName;

    @PatchValidation(groups = Marker.OnPatch.class,
            message = "The student's last name must contain at least one non-whitespace character. Can be null")
    @NotBlank(groups = Marker.OnPostPut.class,
            message = "The student's last name must not be null and must contain at least one non-whitespace character")
    @Size(min = 2, message = "The student's last name must consist of at least two characters")
    private String lastName;

    private GroupDto group;
    private Set<CourseDto> courses;
}

