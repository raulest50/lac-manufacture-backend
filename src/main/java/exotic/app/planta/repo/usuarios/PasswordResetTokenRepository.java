package exotic.app.planta.repo.usuarios;


import exotic.app.planta.model.users.User;
import exotic.app.planta.model.users.auth.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for managing password reset tokens.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find a token by its value.
     * 
     * @param token the token value
     * @return the PasswordResetToken entity if found, null otherwise
     */
    PasswordResetToken findByToken(String token);

    /**
     * Delete all tokens associated with a specific user.
     * 
     * @param user the user whose tokens should be deleted
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = :user")
    void deleteByUser(@Param("user") User user);
}
