package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.users.Role;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.usuarios.RoleRepository;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final String ROLE_MASTER = "ROLE_MASTER";
    private final String ROLE_COMPRAS = "ROLE_COMPRAS";
    private final String ROLE_JEFE_PRODUCCION = "ROLE_JEFE_PRODUCCION";
    private final String ROLE_ASISTENTE_PRODUCCION = "ROLE_ASISTENTE_PRODUCCION";
    private final String ROLE_ALMACEN = "ROLE_ALMACEN";

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {

            // Check or create the "master" user
            Role finalRoleMaster = initRole(ROLE_MASTER);
            initRole(ROLE_COMPRAS);
            initRole(ROLE_JEFE_PRODUCCION);
            initRole(ROLE_ASISTENTE_PRODUCCION);
            initRole(ROLE_ALMACEN);
            userRepository.findByUsername("master").orElseGet(() -> {
                User master = User.builder()
                        .username("master")
                        .password("m1243") // <-- No password encoding (development only!)
                        .roles(Set.of(finalRoleMaster))
                        .build();
                return userRepository.save(master);
            });

        };
    }

    private Role initRole(String role_name){
        Role role = roleRepository.findByName(role_name);
        if (role == null) {
            role = roleRepository.save(new Role(null, role_name));
        }
        return role;
    }
}
