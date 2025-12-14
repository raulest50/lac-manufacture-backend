package lacosmetics.planta.lacmanufacture.repo.producto.manufacturing.snapshots;

import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.snapshots.ManufacturingVersions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManufacturingVersionRepo extends JpaRepository<ManufacturingVersions, Long> {
    Optional<ManufacturingVersions> findTopByProductoOrderByVersionNumberDesc(Producto producto);
}
