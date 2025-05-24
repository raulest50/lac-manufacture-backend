package lacosmetics.planta.lacmanufacture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password-related beans.
 * Separated from SecurityConfig to avoid circular dependencies.
 */
@Configuration
public class PasswordConfig {

    /**
     * Provides a BCryptPasswordEncoder for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}