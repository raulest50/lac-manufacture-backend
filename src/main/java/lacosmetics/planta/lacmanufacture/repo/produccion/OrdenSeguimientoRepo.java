package lacosmetics.planta.lacmanufacture.repo.produccion;

import lacosmetics.planta.lacmanufacture.model.OrdenSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrdenSeguimientoRepo extends JpaRepository<OrdenSeguimiento, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE OrdenSeguimiento o SET o.estado = :estado, o.fechaFinalizacion = CURRENT_TIMESTAMP WHERE o.seguimientoId = :id")
    void updateEstadoById(@Param("id") int id, @Param("estado") int estado);

}
