package exotic.app.planta.repo.activos.fijos.gestion;

import exotic.app.planta.model.activos.fijos.gestion.IncorporacionActivoLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncorporacionActivoLineRepo extends JpaRepository<IncorporacionActivoLine, Long> {
}
