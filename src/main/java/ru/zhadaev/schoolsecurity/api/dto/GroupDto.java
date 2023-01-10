package ru.zhadaev.schoolsecurity.api.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class GroupDto {
    private UUID id;

    @NotBlank(message = "The group name must not be null and must contain at least one non-whitespace character")
    private String name;
}
