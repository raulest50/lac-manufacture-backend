package lacosmetics.planta.lacmanufacture.repo.produccion;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdenProduccionRepo extends JpaRepository<OrdenProduccion, Integer> {

    List<OrdenProduccion> findByResponsableIdAndEstadoOrden(int seccionResponsable, int estadoOrden);

    List<OrdenProduccion> findByEstadoOrden(int estadoOrden);


    /**
     * Finds OrdenProduccion within a date range and estadoOrden.
     * If estadoOrden is 2, it ignores the estadoOrden filter.
     *
     * @param startDate   Start of the date range.
     * @param endDate     End of the date range.
     * @param estadoOrden Estado of the orden.
     * @param pageable    Pagination information.
     * @return Page of OrdenProduccion matching the criteria.
     */
    @EntityGraph(attributePaths = {"ordenesSeguimiento", "producto"})
    @Query("SELECT o FROM OrdenProduccion o WHERE o.fechaInicio BETWEEN :startDate AND :endDate " +
            "AND (:estadoOrden = 2 OR o.estadoOrden = :estadoOrden)")
    Page<OrdenProduccion> findByFechaInicioBetweenAndEstadoOrden(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("estadoOrden") int estadoOrden,
            Pageable pageable
    );


    @Transactional
    @Modifying
    @Query("UPDATE OrdenProduccion o SET o.estadoOrden = :estadoOrden, o.fechaFinal = CURRENT_TIMESTAMP WHERE o.ordenId = :id")
    void updateEstadoOrdenById(@Param("id") int id, @Param("estadoOrden") int estadoOrden);

    @EntityGraph(attributePaths = {"ordenesSeguimiento", "producto"})
    Page<OrdenProduccion> findByResponsableIdAndEstadoOrden(int responsableId, int estadoOrden, Pageable pageable);


}
