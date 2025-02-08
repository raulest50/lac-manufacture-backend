package lacosmetics.planta.lacmanufacture.repo.usuarios;

import lacosmetics.planta.lacmanufacture.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

