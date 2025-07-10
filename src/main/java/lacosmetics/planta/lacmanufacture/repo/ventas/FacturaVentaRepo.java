package lacosmetics.planta.lacmanufacture.repo.ventas;

import lacosmetics.planta.lacmanufacture.model.ventas.FacturaVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacturaVentaRepo extends JpaRepository<FacturaVenta, Integer> {

    @Query("SELECT f FROM FacturaVenta f WHERE (:clienteId IS NULL OR f.cliente.clienteId = :clienteId) " +
            "AND (:estadoPago IS NULL OR f.estadoPago = :estadoPago)")
    Page<FacturaVenta> findByClienteAndEstado(
            @Param("clienteId") Integer clienteId,
            @Param("estadoPago") String estadoPago,
            Pageable pageable
    );
}
