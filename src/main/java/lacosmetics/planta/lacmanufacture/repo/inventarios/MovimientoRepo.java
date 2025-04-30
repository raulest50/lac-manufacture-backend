package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.Movimientos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimientoRepo extends JpaRepository<Movimientos, Integer> {

    @Query("SELECT COALESCE(SUM(m.cantidad), 0) FROM Movimientos m WHERE m.producto.productoId = :productoId")
    Double findTotalCantidadByProductoId(@Param("productoId") int productoId);

    List<Movimientos> findMovimientosByCantidad(Double cantidad);

    // New method
    Page<Movimientos> findByProducto_ProductoIdOrderByFechaMovimientoDesc(int productoId, Pageable pageable);

}
