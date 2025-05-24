package lacosmetics.planta.lacmanufacture.service;

import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lacosmetics.planta.lacmanufacture.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for authentication-related operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Authenticates a user and returns a JWT token
     *
     * @param username the username
     * @param password the password
     * @return a map containing the JWT token and username
     * @throws AuthenticationException if authentication fails
     */
    @Transactional
    public Map<String, String> authenticateUser(String username, String password) {
        try {
            // Create authentication object
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    username,
                    password
            );

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(authRequest);

            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);

            // Prepare response
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", username);

            return response;
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new AuthenticationException("Authentication failed: " + e.getMessage()) {};
        }
    }
}
