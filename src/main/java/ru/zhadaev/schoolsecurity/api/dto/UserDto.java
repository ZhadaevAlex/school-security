package ru.zhadaev.schoolsecurity.api.dto;

import lombok.Data;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import ru.zhadaev.schoolsecurity.api.validation.PatchValidation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;

    @PatchValidation(groups = Marker.OnPatch.class,
            message = "The user's login must contain at least one non-whitespace character. Can be null")
    @NotBlank(groups = Marker.OnPostPut.class,
            message = "The user's login must not be null and must contain at least one non-whitespace character")
    private String login;

    @PatchValidation(groups = Marker.OnPatch.class,
            message = "The user's password must contain at least one non-whitespace character. Can be null")
    @NotBlank(groups = Marker.OnPostPut.class,
            message = "The user's password must not be null and must contain at least one non-whitespace character")
    private String password;

    @NotNull(groups = Marker.OnPostPut.class,
            message = "The user's permissions must be not null")
    private Set<PermissionDto> permissions;
}

