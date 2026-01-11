package exotic.app.planta.repo.inventarios;

import exotic.app.planta.model.inventarios.TransaccionAlmacen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransaccionAlmacenHeaderRepo extends JpaRepository<TransaccionAlmacen, Integer> {

    /**
     * Cuenta las transacciones de almacén con un estado contable y tipo de entidad causante específicos.
     * 
     * @param estadoContable Estado contable de las transacciones
     * @param tipoEntidadCausante Tipo de entidad causante
     * @return Número de transacciones que cumplen con los filtros
     */
    long countByEstadoContableAndTipoEntidadCausante(
        TransaccionAlmacen.EstadoContable estadoContable,
        TransaccionAlmacen.TipoEntidadCausante tipoEntidadCausante
    );

    /**
     * Consulta dinámica para filtrar transacciones de almacén por estado contable, tipo de entidad causante y rango de fechas.
     * 
     * @param estadoContable Estado contable de las transacciones (opcional)
     * @param tipoEntidadCausante Tipo de entidad causante (requerido)
     * @param fechaInicio Fecha inicial del rango (opcional)
     * @param fechaFin Fecha final del rango (opcional)
     * @param pageable Configuración de paginación
     * @return Página de transacciones que cumplen con los filtros
     */
    @Query("SELECT t FROM TransaccionAlmacen t WHERE " +
           "(:estadoContable IS NULL OR t.estadoContable = :estadoContable) AND " +
           "t.tipoEntidadCausante = :tipoEntidadCausante AND " +
           "(:fechaInicio IS NULL OR t.fechaTransaccion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR t.fechaTransaccion <= :fechaFin)")
    Page<TransaccionAlmacen> findByFilters(
        @Param("estadoContable") TransaccionAlmacen.EstadoContable estadoContable,
        @Param("tipoEntidadCausante") TransaccionAlmacen.TipoEntidadCausante tipoEntidadCausante,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        Pageable pageable
    );

    List<TransaccionAlmacen> findByTipoEntidadCausanteAndIdEntidadCausante(
        TransaccionAlmacen.TipoEntidadCausante tipoEntidadCausante,
        int idEntidadCausante
    );

    /**
     * Busca una transacción por ID cargando sus movimientos con fetch join.
     * Incluye las relaciones de producto y lote para evitar N+1 queries.
     *
     * @param transaccionId ID de la transacción
     * @return Transacción con movimientos cargados
     */
    @Query("SELECT DISTINCT t FROM TransaccionAlmacen t " +
           "LEFT JOIN FETCH t.movimientosTransaccion m " +
           "LEFT JOIN FETCH m.producto p " +
           "LEFT JOIN FETCH m.lote l " +
           "WHERE t.transaccionId = :transaccionId")
    Optional<TransaccionAlmacen> findByIdWithMovimientos(@Param("transaccionId") int transaccionId);

    /**
     * Busca transacciones por tipo y entidad causante cargando movimientos con fetch join.
     * Incluye las relaciones de producto y lote para evitar N+1 queries.
     *
     * @param tipoEntidadCausante Tipo de entidad causante
     * @param idEntidadCausante ID de la entidad causante
     * @return Lista de transacciones con movimientos cargados
     */
    @Query("SELECT DISTINCT t FROM TransaccionAlmacen t " +
           "LEFT JOIN FETCH t.movimientosTransaccion m " +
           "LEFT JOIN FETCH m.producto p " +
           "LEFT JOIN FETCH m.lote l " +
           "WHERE t.tipoEntidadCausante = :tipoEntidadCausante " +
           "AND t.idEntidadCausante = :idEntidadCausante")
    List<TransaccionAlmacen> findByTipoEntidadCausanteAndIdEntidadCausanteWithMovimientos(
        @Param("tipoEntidadCausante") TransaccionAlmacen.TipoEntidadCausante tipoEntidadCausante,
        @Param("idEntidadCausante") int idEntidadCausante
    );
}
