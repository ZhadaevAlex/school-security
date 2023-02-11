package ru.zhadaev.schoolsecurity.api.dto;

import lombok.Data;
import ru.zhadaev.schoolsecurity.enums.RoleName;

@Data
public class RoleDto {
    private RoleName name;
}
