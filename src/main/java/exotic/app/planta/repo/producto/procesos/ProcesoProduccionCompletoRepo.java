package exotic.app.planta.repo.producto.procesos;

import exotic.app.planta.model.producto.manufacturing.procesos.ProcesoProduccionCompleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcesoProduccionCompletoRepo extends JpaRepository<ProcesoProduccionCompleto, Integer>, JpaSpecificationExecutor<ProcesoProduccionCompleto> {
    // Métodos personalizados pueden agregarse aquí si son necesarios en el futuro
}