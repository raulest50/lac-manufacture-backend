package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.usuarios.AccesoRepository;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersInitializer {

    private final AccesoRepository accesoRepository;
    private final UserRepository userRepository;

    public void initializeUsers() {
        // Solo inicializar el usuario master con accesos directos
        userRepository.findByUsername("master").orElseGet(() -> {
            String username = "master";
            String rawPassword = "m1243";

            User master = User.builder()
                    .username(username)
                    .password(PasswordConfig.encodePassword(rawPassword, username)) // Properly encoded with Argon2
                    .build();
            return userRepository.save(master);
        });
    }
}
