package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.repo.usuarios.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataBaseInitializer {

    private final UsersInitializer usersInitializer;
    private final CargaMasiva cargaMasiva;
    private final RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(DataBaseInitializer.class);

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Check if the roles table is empty (you can also check other key tables if desired)
            // this check is to see if db is already initialized.
            if (roleRepository.count() == 0) { // if not initialized, then it does the carga masiva
                log.info("Database is empty. Performing initial data setup...");
                usersInitializer.initializeUsers();
                cargaMasiva.executeCargaMasiva();
            } else { // if already initialzed then do nothing
                log.info("Database is already initialized. Skipping insert initialization.");
            }
        };
    }
}
