package lacosmetics.planta.lacmanufacture.repo.producto.manufacturing;

import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.ManufacturingVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturingVersionRepo extends JpaRepository<ManufacturingVersion, Long> {

    List<ManufacturingVersion> findByProducto_ProductoId(String productoId);
}
