package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.real.MovimientoReal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimientoRepo extends JpaRepository<MovimientoReal, Integer> {

    @Query("SELECT COALESCE(SUM(m.cantidad), 0) FROM MovimientoReal m WHERE m.producto.productoId = :productoId")
    Double findTotalCantidadByProductoId(@Param("productoId") int productoId);

    List<MovimientoReal> findMovimientosByCantidad(Double cantidad);

    // New method
    Page<MovimientoReal> findByProducto_ProductoIdOrderByFechaMovimientoDesc(int productoId, Pageable pageable);

}
