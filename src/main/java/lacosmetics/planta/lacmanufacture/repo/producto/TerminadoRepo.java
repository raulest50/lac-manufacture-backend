package lacosmetics.planta.lacmanufacture.repo.producto;

import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


// removed extend : , JpaSpecificationExecutor<Terminado>

@Repository
public interface TerminadoRepo extends JpaRepository<Terminado, Integer>, JpaSpecificationExecutor<Terminado> {

    List<Terminado> findByInsumos_Producto(Producto producto);

}
