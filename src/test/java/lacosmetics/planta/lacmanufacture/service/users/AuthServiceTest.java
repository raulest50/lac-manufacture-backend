package lacosmetics.planta.lacmanufacture.service.users;

import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.model.users.auth.PasswordResetToken;
import lacosmetics.planta.lacmanufacture.repo.usuarios.PasswordResetTokenRepository;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lacosmetics.planta.lacmanufacture.security.JwtTokenProvider;
import lacosmetics.planta.lacmanufacture.service.commons.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    void authenticateUser_inactiveUser_throwsException() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        PasswordResetTokenRepository tokenRepo = Mockito.mock(PasswordResetTokenRepository.class);
        EmailService emailService = Mockito.mock(EmailService.class);

        AuthService service = new AuthService(userRepo, authManager, tokenProvider, tokenRepo, emailService);

        User inactive = new User();
        inactive.setEstado(2);
        when(userRepo.findByUsername("user")).thenReturn(Optional.of(inactive));

        assertThrows(AuthenticationException.class, () -> service.authenticateUser("user", "pass"));
        verify(authManager, never()).authenticate(any());
    }

    @Test
    void requestPasswordReset_inactiveUser_returnsFalse() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        PasswordResetTokenRepository tokenRepo = Mockito.mock(PasswordResetTokenRepository.class);
        EmailService emailService = Mockito.mock(EmailService.class);

        AuthService service = new AuthService(userRepo, authManager, tokenProvider, tokenRepo, emailService);

        User inactive = new User();
        inactive.setEstado(2);
        inactive.setUsername("user");
        inactive.setNombreCompleto("User Test");
        inactive.setEmail("user@test.com");
        when(userRepo.findByEmail("user@test.com")).thenReturn(Optional.of(inactive));

        boolean result = service.requestPasswordReset("user@test.com");

        assertFalse(result);
        verify(tokenRepo, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString());
    }
}
