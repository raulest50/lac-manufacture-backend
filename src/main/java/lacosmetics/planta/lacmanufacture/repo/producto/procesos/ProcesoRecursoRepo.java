package lacosmetics.planta.lacmanufacture.repo.producto.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoRecurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcesoRecursoRepo extends JpaRepository<ProcesoRecurso, Long>, JpaSpecificationExecutor<ProcesoRecurso> {
    // Custom query methods can be added here if needed
}