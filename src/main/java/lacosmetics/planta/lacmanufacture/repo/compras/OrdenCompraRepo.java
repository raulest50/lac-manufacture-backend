package lacosmetics.planta.lacmanufacture.repo.compras;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenCompraRepo extends JpaRepository<OrdenCompraMateriales, Integer> {

    Page<OrdenCompraMateriales> findByFechaEmisionBetweenAndEstadoIn(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<Integer> estados,
            Pageable pageable
    );

    @Query("""
            SELECT orden
            FROM OrdenCompraMateriales orden
            WHERE orden.fechaEmision BETWEEN :startDate AND :endDate
            AND orden.estado IN :estados
            AND (:proveedorId IS NULL OR orden.proveedor.id = :proveedorId)
            """)
    Page<OrdenCompraMateriales> findByFechaEmisionBetweenAndEstadoInAndProveedor(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("estados") List<Integer> estados,
            @Param("proveedorId") String proveedorId,
            Pageable pageable
    );

    Optional<OrdenCompraMateriales> findByOrdenCompraIdAndEstado(Integer ordenCompraId, int estado);

    /**
     * Verifica si existe al menos una orden de compra con el estado especificado
     * @param estado el estado a buscar
     * @return true si existe al menos una orden con ese estado, false en caso contrario
     */
    boolean existsByEstado(int estado);
}
