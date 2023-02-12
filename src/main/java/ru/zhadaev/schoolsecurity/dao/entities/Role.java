package ru.zhadaev.schoolsecurity.dao.entities;

import lombok.Data;
import ru.zhadaev.schoolsecurity.enums.RoleName;

import javax.persistence.*;

@Entity
@Data
@Table(schema = "school", name = "roles")
public class Role {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private RoleName name;
}
