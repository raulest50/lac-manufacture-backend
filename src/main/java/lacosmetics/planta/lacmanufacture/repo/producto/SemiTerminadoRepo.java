package lacosmetics.planta.lacmanufacture.repo.producto;

import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemiTerminadoRepo extends JpaRepository<SemiTerminado, Integer>, JpaSpecificationExecutor<SemiTerminado> {

    List<SemiTerminado> findByInsumos_Producto(Producto producto);

}

