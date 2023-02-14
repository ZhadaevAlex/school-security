package ru.zhadaev.schoolsecurity.dao.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(schema = "school", name = "permissions")
public class Permission {
    @Id
    @Column(name = "permission_name")
    private String name;

    @Column(name = "permission_description")
    private String description;
}
