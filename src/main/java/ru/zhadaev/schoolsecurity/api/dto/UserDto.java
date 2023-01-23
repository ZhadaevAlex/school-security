package ru.zhadaev.schoolsecurity.api.dto;

import lombok.Data;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.api.validation.PatchValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;

    @PatchValidation(groups = Marker.OnPatch.class,
            message = "The user's name must contain at least one non-whitespace character. Can be null")
    @NotBlank(groups = Marker.OnPostPut.class,
            message = "The user's name must not be null and must contain at least one non-whitespace character")
    @Size(min = 2, message = "The user's name must consist of at least two characters")
    private String username;

    @NotBlank(groups = Marker.OnPostPut.class,
            message = "The user's name must not be null and must contain at least one non-whitespace character")
    @Size(min = 1, message = "The user's name must consist of at least one characters")
    private String password;

    private Collection<RoleDto> roles;
}

