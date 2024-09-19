package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.GrupoMovimeintoMP;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoMovimientoMpRepo extends JpaRepository<GrupoMovimeintoMP, Integer>, JpaSpecificationExecutor<GrupoMovimeintoMP> {


}
