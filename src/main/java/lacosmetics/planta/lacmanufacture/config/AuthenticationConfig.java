package lacosmetics.planta.lacmanufacture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/**
 * Configuration class for authentication-related beans.
 * Separated from SecurityConfig to avoid circular dependencies.
 */
@Configuration
public class AuthenticationConfig {

    /**
     * Provides an AuthenticationManager bean for use in the application
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}