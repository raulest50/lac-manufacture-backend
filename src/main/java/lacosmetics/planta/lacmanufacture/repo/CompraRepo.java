package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.compras.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CompraRepo extends JpaRepository<Compra, Integer> {

    Page<Compra> findByProveedorIdAndFechaCompraBetween(
            int proveedorId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

}
