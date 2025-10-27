package lacosmetics.planta.lacmanufacture.repo.usuarios;

import lacosmetics.planta.lacmanufacture.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    /**
     * Find users by estado (1 = active, 2 = inactive)
     * @param estado the estado to filter by
     * @return list of users with the specified estado
     */
    List<User> findByEstado(int estado);
}
