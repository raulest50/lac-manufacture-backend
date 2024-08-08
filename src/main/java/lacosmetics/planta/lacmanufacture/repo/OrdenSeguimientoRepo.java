package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.OrdenSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrdenSeguimientoRepo extends JpaRepository<OrdenSeguimiento, Integer> {

    List<OrdenSeguimiento> findBySeccionResponsable(int zonaId);

    @Transactional
    @Modifying
    @Query("UPDATE OrdenSeguimiento o SET o.estado = :estado WHERE o.seguimientoId = :id")
    void updateEstadoById(int id, int estado);

}
