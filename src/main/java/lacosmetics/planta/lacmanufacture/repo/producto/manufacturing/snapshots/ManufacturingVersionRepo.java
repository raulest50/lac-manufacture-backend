package lacosmetics.planta.lacmanufacture.repo.producto.manufacturing.snapshots;

import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.snapshots.ManufacturingVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManufacturingVersionRepo extends JpaRepository<ManufacturingVersion, Long> {
    Optional<ManufacturingVersion> findTopByProductoOrderByVersionNumberDesc(Producto producto);
}
