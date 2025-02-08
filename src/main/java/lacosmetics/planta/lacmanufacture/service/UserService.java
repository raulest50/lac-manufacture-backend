package lacosmetics.planta.lacmanufacture.service;

import lacosmetics.planta.lacmanufacture.model.users.Role;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Given a username, find the user in the DB, map roles to GrantedAuthorities
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<GrantedAuthority> authorities = appUser.getRoles().stream()
                .map(Role::getName) // e.g. "ROLE_MASTER"
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                appUser.getUsername(),
                appUser.getPassword(),
                authorities
        );
    }
}

