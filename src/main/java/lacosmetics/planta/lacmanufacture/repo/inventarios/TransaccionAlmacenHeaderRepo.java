package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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
}
