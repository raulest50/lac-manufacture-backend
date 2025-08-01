package lacosmetics.planta.lacmanufacture.repo.activos.fijos.compras;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.FacturaCompraActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaCompraActivoRepo extends JpaRepository<FacturaCompraActivo, Integer> {
}
