package lacosmetics.planta.lacmanufacture.repo.producto;

import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepo extends JpaRepository<Producto, String>, JpaSpecificationExecutor<Producto> {

    Optional<Producto> findByProductoId(String id);

}
