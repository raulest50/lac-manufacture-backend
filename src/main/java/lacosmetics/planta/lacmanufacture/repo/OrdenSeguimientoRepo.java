package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenSeguimientoRepo extends JpaRepository<OrdenProduccion, Integer> {

}
