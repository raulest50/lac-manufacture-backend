package lacosmetics.planta.lacmanufacture.repo.activos.fijos.gestion;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.gestion.IncorporacionActivoHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncorporacionActivoHeaderRepo extends JpaRepository<IncorporacionActivoHeader, Long> {
}
