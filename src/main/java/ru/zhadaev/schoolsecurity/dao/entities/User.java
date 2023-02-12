package ru.zhadaev.schoolsecurity.dao.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(schema = "school", name = "users")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "user_username")
    private String username;

    @Column(name = "user_password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_name")
    private Role role;
}