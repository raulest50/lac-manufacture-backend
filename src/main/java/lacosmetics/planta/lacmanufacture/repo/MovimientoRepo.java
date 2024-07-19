package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimientoRepo extends JpaRepository<Movimiento, Integer> {

    @Query("SELECT SUM(m.cantidad) FROM Movimiento m WHERE m.producto.producto_id = :producto_id")
    Double findTotalCantidadByProductoId(@Param("producto_id") int producto_id);

    List<Movimiento> findMovimientosByCantidad(Double producto_id);
}
