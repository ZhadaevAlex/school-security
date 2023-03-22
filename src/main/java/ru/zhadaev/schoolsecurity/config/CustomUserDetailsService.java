package ru.zhadaev.schoolsecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.zhadaev.schoolsecurity.dao.entities.Permission;
import ru.zhadaev.schoolsecurity.dao.entities.User;
import ru.zhadaev.schoolsecurity.dao.repositories.UserRepository;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByLogin(username).orElseThrow(
                () -> new UsernameNotFoundException(username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(this.getPermissions(user))
                .build();
    }

    private Set<GrantedAuthority> getPermissions(User user) {
        return user.getPermissions()
                .stream()
                .map(Permission::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
