package lacosmetics.planta.lacmanufacture.repo.producto.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos.ProcesoProduccionCompleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcesoProduccionCompletoRepo extends JpaRepository<ProcesoProduccionCompleto, Integer>, JpaSpecificationExecutor<ProcesoProduccionCompleto> {
    // Métodos personalizados pueden agregarse aquí si son necesarios en el futuro
}