package exotic.app.planta.repo.producto.procesos;

import exotic.app.planta.model.producto.manufacturing.procesos.RecursoProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecursoProduccionRepo extends JpaRepository<RecursoProduccion, Long>, JpaSpecificationExecutor<RecursoProduccion> {
    Optional<RecursoProduccion> findByNombre(String nombre);
}
