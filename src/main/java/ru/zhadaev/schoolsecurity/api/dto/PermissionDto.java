package ru.zhadaev.schoolsecurity.api.dto;

import lombok.Data;
import ru.zhadaev.schoolsecurity.api.validation.Marker;
import javax.validation.constraints.NotBlank;

@Data
public class PermissionDto {
    @NotBlank(groups = Marker.OnPostPut.class,
            message = "The permission name must not be null and must contain at least one non-whitespace character")
    private String name;

    private String description;
}
