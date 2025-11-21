package lacosmetics.planta.lacmanufacture.repo.compras;

import lacosmetics.planta.lacmanufacture.model.compras.FacturaCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface FacturaCompraRepo extends JpaRepository<FacturaCompra, Integer> {

    Page<FacturaCompra> findByProveedorIdAndFechaCompraBetween(
            String proveedorId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

}
