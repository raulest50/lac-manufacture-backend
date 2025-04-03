package lacosmetics.planta.lacmanufacture.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataBaseInitializer {

    private final UsersInitializer usersInitializer;
    private final CargaMasiva cargaMasiva;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            usersInitializer.initializeUsers();
            cargaMasiva.executeCargaMasiva();
        };
    }
}
