package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lacosmetics.planta.lacmanufacture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // For development, allow all authenticated users to access any endpoint
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();
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

