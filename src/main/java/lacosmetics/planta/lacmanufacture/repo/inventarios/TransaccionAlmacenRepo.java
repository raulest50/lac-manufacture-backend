package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransaccionAlmacenRepo extends JpaRepository<Movimiento, Integer> {

    @Query("SELECT COALESCE(SUM(m.cantidad), 0) FROM Movimiento m WHERE m.producto.productoId = :productoId")
    Double findTotalCantidadByProductoId(@Param("productoId") String productoId);

    List<Movimiento> findMovimientosByCantidad(Double cantidad);

    // Get movimientos filtered by product ID
    List<Movimiento> findByProducto_ProductoId(String productoId);

    // New method
    Page<Movimiento> findByProducto_ProductoIdOrderByFechaMovimientoDesc(String productoId, Pageable pageable);

}
