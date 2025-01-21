package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.Role;
import lacosmetics.planta.lacmanufacture.model.User;
import lacosmetics.planta.lacmanufacture.repo.RoleRepository;
import lacosmetics.planta.lacmanufacture.repo.UserRepository;
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

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Check or create the roles
            Role roleMaster = roleRepository.findByName("ROLE_MASTER");
            if (roleMaster == null) {
                roleMaster = roleRepository.save(new Role(null, "ROLE_MASTER"));
            }

            Role roleWorker = roleRepository.findByName("ROLE_WORKER");
            if (roleWorker == null) {
                roleWorker = roleRepository.save(new Role(null, "ROLE_WORKER"));
            }

            // Check or create the "master" user
            Role finalRoleMaster = roleMaster;
            userRepository.findByUsername("master").orElseGet(() -> {
                User master = User.builder()
                        .username("master")
                        .password("{noop}masterpass") // <-- No password encoding (development only!)
                        .roles(Set.of(finalRoleMaster))
                        .build();
                return userRepository.save(master);
            });

            // Check or create the "worker" user
            Role finalRoleWorker = roleWorker;
            userRepository.findByUsername("worker").orElseGet(() -> {
                User worker = User.builder()
                        .username("worker")
                        .password("{noop}workerpass")
                        .roles(Set.of(finalRoleWorker))
                        .build();
                return userRepository.save(worker);
            });
        };
    }
}
