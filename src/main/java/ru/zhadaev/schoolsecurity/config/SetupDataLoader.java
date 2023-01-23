package ru.zhadaev.schoolsecurity.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.zhadaev.schoolsecurity.dao.entities.Role;
import ru.zhadaev.schoolsecurity.dao.entities.User;
import ru.zhadaev.schoolsecurity.dao.repositories.RoleRepository;
import ru.zhadaev.schoolsecurity.dao.repositories.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup = false;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_ROLE = "USER";
    private static final String TEACHER_ROLE = "TEACHER";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String MANAGER_ROLE = "MANAGER";
    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    private static final String USER_USERNAME = "user";
    private static final String USER_PASSWORD = "userPass";
    private static final String TEACHER_USERNAME = "teacher";
    private static final String TEACHER_PASSWORD = "teacherPass";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "adminPass";
    private static final String MANAGER_USERNAME = "manager";
    private static final String MANAGER_PASSWORD = "managerPass";
    private static final String SUPER_ADMIN_USERNAME = "super_admin";
    private static final String SUPER_ADMIN_PASSWORD = "superAdminPass";


    @SneakyThrows
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        Role userRole = createRoleIfNotFound(USER_ROLE);
        Role teacherRole = createRoleIfNotFound(TEACHER_ROLE);
        Role adminRole = createRoleIfNotFound(ADMIN_ROLE);
        Role managerRole = createRoleIfNotFound(MANAGER_ROLE);
        Role superAdminRole = createRoleIfNotFound(SUPER_ADMIN_ROLE);

        createUserIfNotFound(USER_USERNAME, USER_PASSWORD, Collections.singletonList(userRole));
        createUserIfNotFound(TEACHER_USERNAME, TEACHER_PASSWORD, Collections.singletonList(teacherRole));
        createUserIfNotFound(ADMIN_USERNAME, ADMIN_PASSWORD, Arrays.asList(userRole, adminRole));
        createUserIfNotFound(MANAGER_USERNAME, MANAGER_PASSWORD, Collections.singletonList(managerRole));
        createUserIfNotFound(SUPER_ADMIN_USERNAME, SUPER_ADMIN_PASSWORD, Collections.singletonList(superAdminRole));

        alreadySetup = true;
    }

    @Transactional
    public void createUserIfNotFound(String username,
                                     String password,
                                     Collection<Role> roles) {
        userRepository.findByUsername(username)
                .orElseGet(() ->
                {
                    User userCreated = new User();
                    userCreated.setUsername(username);
                    userCreated.setPassword(passwordEncoder.encode(password));
                    userCreated.setRoles(roles);
                    userRepository.save(userCreated);
                    return userCreated;
                });
    }

    @Transactional
    public Role createRoleIfNotFound(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() ->
                {
                    Role roleCreated = new Role();
                    roleCreated.setName(name);
                    roleRepository.save(roleCreated);
                    return roleCreated;
                });
    }
}