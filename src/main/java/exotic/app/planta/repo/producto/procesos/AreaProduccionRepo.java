package exotic.app.planta.repo.producto.procesos;

import exotic.app.planta.model.producto.manufacturing.procesos.AreaProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaProduccionRepo extends JpaRepository<AreaProduccion, Integer>, JpaSpecificationExecutor<AreaProduccion> {
    Optional<AreaProduccion> findByNombre(String nombre);
}
