package lacosmetics.planta.lacmanufacture.repo.activos.fijos;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.OrdenCompraActivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de persistencia de las órdenes de compra de activos fijos.
 */
@Repository
public interface OrdenCompraActivoRepo extends JpaRepository<OrdenCompraActivo, Integer> {
    
    /**
     * Busca órdenes de compra por rango de fechas y estados.
     * 
     * @param startDate fecha inicial
     * @param endDate fecha final
     * @param estados lista de estados a filtrar
     * @param pageable configuración de paginación
     * @return página de órdenes de compra que cumplen con los criterios
     */
    Page<OrdenCompraActivo> findByFechaEmisionBetweenAndEstadoIn(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<Integer> estados,
            Pageable pageable
    );
    
    /**
     * Busca una orden de compra por ID y estado.
     * 
     * @param ordenCompraActivoId ID de la orden de compra
     * @param estado estado de la orden
     * @return orden de compra si existe
     */
    Optional<OrdenCompraActivo> findByOrdenCompraActivoIdAndEstado(Integer ordenCompraActivoId, int estado);
    
    /**
     * Verifica si existe al menos una orden de compra con el estado especificado.
     * 
     * @param estado el estado a buscar
     * @return true si existe al menos una orden con ese estado, false en caso contrario
     */
    boolean existsByEstado(int estado);
}