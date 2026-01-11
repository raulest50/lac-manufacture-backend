package exotic.app.planta.repo.master.configs;

import exotic.app.planta.model.master.configs.MasterDirective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterDirectiveRepo extends JpaRepository<MasterDirective, Long> {
    // Consultas personalizadas si son necesarias
    Optional<MasterDirective> findByNombre(String nombre);
}