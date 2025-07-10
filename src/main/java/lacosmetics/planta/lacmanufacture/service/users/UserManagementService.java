// src/main/java/lacosmetics/planta/lacmanufacture/service/UserManagementService.java
package lacosmetics.planta.lacmanufacture.service.users;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.config.PasswordConfig;
import lacosmetics.planta.lacmanufacture.model.users.Acceso;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.repo.usuarios.AccesoRepository;
import lacosmetics.planta.lacmanufacture.repo.usuarios.PasswordResetTokenRepository;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final TransaccionAlmacenHeaderRepo transaccionAlmacenHeaderRepo;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

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

    /**
     * Deactivates a user by setting their estado to 2 (inactive)
     * @param userId the ID of the user to deactivate
     * @return the updated user
     */
    public User deactivateUser(Long userId) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("master".equalsIgnoreCase(existing.getUsername())) {
            throw new RuntimeException("Cannot deactivate master user");
        }

        // Only deactivate if the user is active (estado = 1)
        if (existing.getEstado() == 1) {
            existing.setEstado(2); // Set to inactive
            return userRepository.save(existing);
        } else {
            throw new RuntimeException("User is already inactive");
        }
    }

    /**
     * Activates a user by setting their estado to 1 (active)
     * @param userId the ID of the user to activate
     * @return the updated user
     */
    public User activateUser(Long userId) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only activate if the user is inactive (estado = 2)
        if (existing.getEstado() == 2) {
            existing.setEstado(1); // Set to active
            return userRepository.save(existing);
        } else {
            throw new RuntimeException("User is already active");
        }
    }

    /**
     * Deletes a user if there are no referential integrity issues
     * @param userId the ID of the user to delete
     * @throws DataIntegrityViolationException if there are referential integrity issues
     */
    public void deleteUser(Long userId) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("master".equalsIgnoreCase(existing.getUsername())) {
            throw new RuntimeException("Cannot delete master user");
        }

        try {
            // Delete any password reset tokens associated with the user
            passwordResetTokenRepository.deleteByUser(existing);

            // Try to delete the user - this will fail with a DataIntegrityViolationException
            // if there are any referential integrity issues
            userRepository.delete(existing);
        } catch (DataIntegrityViolationException e) {
            // If deletion fails due to referential integrity, throw a more user-friendly exception
            throw new RuntimeException("Cannot delete user because it is referenced by other entities. Consider deactivating the user instead.", e);
        }
    }

    /*public User addAccesoToUser(Long userId, String moduloName) {
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
    }*/

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
