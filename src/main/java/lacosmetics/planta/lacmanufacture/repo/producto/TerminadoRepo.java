package lacosmetics.planta.lacmanufacture.repo.producto;

import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


// removed extend : , JpaSpecificationExecutor<Terminado>

@Repository
public interface TerminadoRepo extends JpaRepository<Terminado, String>, JpaSpecificationExecutor<Terminado> {

    @Query("SELECT t FROM Terminado t JOIN t.currentVersion v JOIN v.insumos i WHERE i.producto = :producto")
    List<Terminado> findByInsumos_Producto(@Param("producto") Producto producto);

}
