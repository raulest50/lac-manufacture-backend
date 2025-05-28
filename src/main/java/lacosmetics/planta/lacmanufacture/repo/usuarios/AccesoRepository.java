package lacosmetics.planta.lacmanufacture.repo.usuarios;
import lacosmetics.planta.lacmanufacture.model.users.Acceso;
import lacosmetics.planta.lacmanufacture.model.users.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccesoRepository extends JpaRepository<Acceso, Long> {
    // Find by module access
    Acceso findByModuloAcceso(Acceso.Modulo moduloAcceso);

    // Find all accesos for a specific user
    List<Acceso> findByUser(User user);
}
