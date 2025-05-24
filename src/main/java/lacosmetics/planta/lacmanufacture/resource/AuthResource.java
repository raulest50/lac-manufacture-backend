package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthResource {

    private final AuthService authService;

    /**
     * Login endpoint that authenticates a user and returns a JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Delegate authentication to the service
            Map<String, String> response = authService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Authentication error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Returns information about the currently authenticated user
     */
    @GetMapping("/whoami")
    public Object whoAmI(Authentication authentication) {
        return authentication;
        // or return a custom DTO with roles, username, etc.
    }

    /**
     * Request body for login
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
