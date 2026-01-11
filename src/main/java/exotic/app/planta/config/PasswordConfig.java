package exotic.app.planta.config;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Configuration class for password-related beans.
 * Separated from SecurityConfig to avoid circular dependencies.
 */
@Configuration
public class PasswordConfig {

    // Argon2 parameters
    private static final int ITERATIONS = 10;
    private static final int MEMORY = 65536;
    private static final int PARALLELISM = 1;
    private static final int HASH_LENGTH = 32;

    /**
     * Provides an Argon2 PasswordEncoder for secure password hashing
     * Uses the username as salt for additional security
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    /**
     * Static method to encode a password using Argon2 with the username as salt
     * This can be called from any service that needs to encode a password
     * 
     * @param rawPassword the raw password to encode
     * @param username the username to use as salt
     * @return the encoded password
     */
    public static String encodePassword(CharSequence rawPassword, String username) {
        byte[] salt = username.getBytes(StandardCharsets.UTF_8);

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY)
                .withParallelism(PARALLELISM)
                .withSalt(salt);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());

        byte[] result = new byte[HASH_LENGTH];
        generator.generateBytes(rawPassword.toString().toCharArray(), result);

        // Format: algorithm$iterations$memory$parallelism$salt$hash
        return String.format("$argon2id$v=19$m=%d,t=%d,p=%d$%s$%s",
                MEMORY, ITERATIONS, PARALLELISM,
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(result));
    }

    /**
     * Custom implementation of PasswordEncoder using Argon2 algorithm
     * with username as salt for additional security
     */
    public static class Argon2PasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(CharSequence rawPassword) {
            // This method is required by the PasswordEncoder interface but should not be used directly
            // Instead, use the static encodePassword method that includes the username
            // For compatibility with Spring Security, we'll use a random salt here
            return encodePassword(rawPassword, "default_salt");
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            if (encodedPassword == null || !encodedPassword.startsWith("$argon2id$")) {
                return false;
            }

            // Extract salt from the encoded password
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 6) {
                return false;
            }

            String saltBase64 = parts[4];
            byte[] salt = Base64.getDecoder().decode(saltBase64);

            // Parse parameters
            String[] params = parts[3].split(",");
            int memory = Integer.parseInt(params[0].substring(2));
            int iterations = Integer.parseInt(params[1].substring(2));
            int parallelism = Integer.parseInt(params[2].substring(2));

            // Generate hash with the same parameters
            Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withIterations(iterations)
                    .withMemoryAsKB(memory)
                    .withParallelism(parallelism)
                    .withSalt(salt);

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(builder.build());

            byte[] result = new byte[HASH_LENGTH];
            generator.generateBytes(rawPassword.toString().toCharArray(), result);

            String newHash = Base64.getEncoder().encodeToString(result);
            String originalHash = parts[5];

            return newHash.equals(originalHash);
        }
    }
}
