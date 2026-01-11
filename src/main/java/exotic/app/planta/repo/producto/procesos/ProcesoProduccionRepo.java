package exotic.app.planta.repo.producto.procesos;

import exotic.app.planta.model.producto.manufacturing.procesos.ProcesoProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcesoProduccionRepo extends JpaRepository<ProcesoProduccion, Integer>, JpaSpecificationExecutor<ProcesoProduccion> {
    Optional<ProcesoProduccion> findByNombre(String nombre);
}