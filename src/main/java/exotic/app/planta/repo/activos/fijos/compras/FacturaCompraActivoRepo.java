package exotic.app.planta.repo.activos.fijos.compras;

import exotic.app.planta.model.activos.fijos.compras.FacturaCompraActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaCompraActivoRepo extends JpaRepository<FacturaCompraActivo, Integer> {
}
