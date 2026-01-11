package exotic.app.planta.resource.users;

import exotic.app.planta.model.users.User;
import exotic.app.planta.model.users.Acceso;
import exotic.app.planta.repo.usuarios.UserRepository;
import exotic.app.planta.service.users.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthResource {

    private final AuthService authService;
    private final UserRepository userRepository;

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
     * with custom response that includes nivel for each authority
     */
    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Obtener el nombre de usuario del principal
        String username = authentication.getName();

        // Buscar el usuario en la base de datos para obtener sus accesos con niveles
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Crear un mapa para la respuesta personalizada
        Map<String, Object> response = new HashMap<>();
        response.put("name", username);
        response.put("authenticated", authentication.isAuthenticated());
        response.put("credentials", authentication.getCredentials());
        response.put("details", authentication.getDetails());

        // Crear una lista de autoridades con nivel
        List<Map<String, Object>> authoritiesWithLevel = new ArrayList<>();

        // Mapear cada acceso a una autoridad con nivel
        for (Acceso acceso : user.getAccesos()) {
            Map<String, Object> authorityWithLevel = new HashMap<>();
            authorityWithLevel.put("authority", "ACCESO_" + acceso.getModuloAcceso().name());
            authorityWithLevel.put("nivel", String.valueOf(acceso.getNivel()));
            authoritiesWithLevel.add(authorityWithLevel);
        }

        response.put("authorities", authoritiesWithLevel);

        // Agregar el principal (información del usuario)
        Map<String, Object> principalInfo = new HashMap<>();
        principalInfo.put("username", username);
        principalInfo.put("password", ""); // No incluir la contraseña real
        principalInfo.put("authorities", authoritiesWithLevel);
        principalInfo.put("accountNonExpired", true);
        principalInfo.put("accountNonLocked", true);
        principalInfo.put("credentialsNonExpired", true);
        principalInfo.put("enabled", true);

        response.put("principal", principalInfo);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for requesting a password reset.
     * If a user with the given email exists, an email with a reset link will be sent.
     * 
     * @param request the request containing the email
     * @return a response indicating success or failure
     */
    @PostMapping("/request_reset_passw")
    public ResponseEntity<?> requestPasswordReset(@RequestBody EmailRequest request) {
        boolean success = authService.requestPasswordReset(request.getEmail());

        // Always return success to prevent user enumeration attacks
        return ResponseEntity.ok(Map.of("message", "If an account with that email exists, a password reset link has been sent."));
    }

    /**
     * Endpoint for setting a new password using a reset token.
     * 
     * @param request the request containing the token and new password
     * @return a response indicating success or failure
     */
    @PostMapping("/set_new_passw")
    public ResponseEntity<?> setNewPassword(@RequestBody PasswordResetRequest request) {
        boolean success = authService.setNewPassword(request.getToken(), request.getNewPassword());

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Password has been reset successfully."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token."));
        }
    }

    /**
     * Request body for email-based requests
     */
    public static class EmailRequest {
        @JsonProperty("email")
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * Request body for password reset
     */
    public static class PasswordResetRequest {
        @JsonProperty("token")
        private String token;

        @JsonProperty("newPassword")
        private String newPassword;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
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
