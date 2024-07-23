package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenProduccionRepo extends JpaRepository<OrdenProduccion, Integer> {

    List<OrdenProduccion> findBySeccionResponsableAndEstadoOrden(int seccionResponsable, int estadoOrden);

    List<OrdenProduccion> findByEstadoOrden(int estadoOrden);

}
