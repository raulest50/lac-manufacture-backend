package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lacosmetics.planta.lacmanufacture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final Environment environment;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Check if we're running in development mode (locally)
        boolean isDevelopment = isDevelopmentEnvironment();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    // For /api/backend-info endpoints:
                    // - In development: allow without authentication
                    // - In production: block access completely
                    if (isDevelopment) {
                        auth.requestMatchers("/api/backend-info/**").permitAll();
                    } else {
                        auth.requestMatchers("/api/backend-info/**").denyAll();
                    }

                    // For all other endpoints, require authentication
                    auth.anyRequest().authenticated();
                })
                .httpBasic(withDefaults());

        return http.build();
    }

    /**
     * Determines if the application is running in a development environment.
     * This is true when running locally and false when running in a Docker container on render.com.
     */
    private boolean isDevelopmentEnvironment() {
        // Check if Spring Boot DevTools is active, which indicates a development environment
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equals("dev")) {
                return true;
            }
        }

        // If no active profiles, check if we're running locally (not in Docker)
        String hostname = System.getenv("HOSTNAME");
        return hostname == null || !hostname.startsWith("render-");
    }

    /**
     * Provide a custom authentication provider that uses our CustomUserDetailsService.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(new UserService(userRepository));
        // In a real app, use a password encoder (e.g. BCryptPasswordEncoder)
        provider.setPasswordEncoder(org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        return provider;
    }
}
