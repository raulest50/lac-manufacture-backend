// src/main/java/lacosmetics/planta/lacmanufacture/service/UserManagementService.java
package lacosmetics.planta.lacmanufacture.service;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.config.PasswordConfig;
import lacosmetics.planta.lacmanufacture.model.users.Role;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.usuarios.RoleRepository;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        // Encrypt the password before saving using Argon2 with username as salt
        user.setPassword(PasswordConfig.encodePassword(user.getPassword(), user.getUsername()));
        return userRepository.save(user);
    }

    public User updateUser(Long userId, User updatedUser) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setUsername(updatedUser.getUsername());

        // Only encrypt the password if it has been changed
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(PasswordConfig.encodePassword(updatedUser.getPassword(), existing.getUsername()));
        }

        existing.setRoles(updatedUser.getRoles());
        return userRepository.save(existing);
    }

    public void deleteUser(Long userId) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ("master".equalsIgnoreCase(existing.getUsername())) {
            throw new RuntimeException("Cannot delete master user");
        }
        userRepository.delete(existing);
    }

    public User addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ("master".equalsIgnoreCase(user.getUsername())) {
            throw new RuntimeException("Cannot remove roles from master user");
        }
        user.getRoles().removeIf(role -> role.getId().equals(roleId));
        return userRepository.save(user);
    }
}
