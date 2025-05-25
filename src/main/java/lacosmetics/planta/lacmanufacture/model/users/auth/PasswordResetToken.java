package lacosmetics.planta.lacmanufacture.model.users.auth;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity for storing password reset tokens.
 * Each token is associated with a user and has an expiry date.
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(1); // Token valid for 1 hour
    }

    /**
     * Checks if the token has expired.
     * 
     * @return true if the token has expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
