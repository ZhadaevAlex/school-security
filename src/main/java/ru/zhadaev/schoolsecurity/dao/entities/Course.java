package ru.zhadaev.schoolsecurity.dao.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "school", name = "courses")
@Data
public class Course {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "course_id")
    private UUID id;

    @Column(name = "course_name")
    private String name;

    @Column(name = "course_description")
    private String description;
}
