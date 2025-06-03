// src/main/java/lacosmetics/planta/lacmanufacture/service/UserManagementService.java
package lacosmetics.planta.lacmanufacture.service.users;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.config.PasswordConfig;
import lacosmetics.planta.lacmanufacture.model.users.Acceso;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.usuarios.AccesoRepository;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final AccesoRepository accesoRepository;
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

        existing.setAccesos(updatedUser.getAccesos());
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

    public User addAccesoToUser(Long userId, String moduloName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Convertir el nombre del módulo a enum
            Acceso.Modulo modulo = Acceso.Modulo.valueOf(moduloName);

            // Crear un nuevo acceso directamente
            return addAccesoToUserByModulo(userId, modulo, 1);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Módulo no válido: " + moduloName);
        }
    }

    public User removeAccesoFromUser(Long userId, Long accesoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ("master".equalsIgnoreCase(user.getUsername())) {
            throw new RuntimeException("Cannot remove accesos from master user");
        }
        user.getAccesos().removeIf(acceso -> acceso.getId().equals(accesoId));
        return userRepository.save(user);
    }

    public User addAccesoToUserByModulo(Long userId, Acceso.Modulo modulo, int nivel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Siempre crear un nuevo acceso directamente
        Acceso acceso = Acceso.builder()
                .user(user)
                .moduloAcceso(modulo)
                .nivel(nivel)
                .build();

        // Guardar el nuevo acceso
        acceso = accesoRepository.save(acceso);

        // Añadir el acceso al usuario
        user.getAccesos().add(acceso);
        return userRepository.save(user);
    }
}
