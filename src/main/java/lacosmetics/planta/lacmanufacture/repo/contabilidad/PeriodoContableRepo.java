package lacosmetics.planta.lacmanufacture.repo.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.PeriodoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad PeriodoContable.
 * Proporciona operaciones CRUD básicas para períodos contables.
 */
@Repository
public interface PeriodoContableRepo extends JpaRepository<PeriodoContable, Long> {
    
    /**
     * Encuentra períodos contables por su estado
     * 
     * @param estado El estado del período contable
     * @return Lista de períodos contables con el estado especificado
     */
    List<PeriodoContable> findByEstado(PeriodoContable.EstadoPeriodo estado);
    
    /**
     * Verifica si existe algún período contable que se solape con el rango de fechas dado
     * 
     * @param fechaFin Fecha fin del rango a verificar
     * @param fechaInicio Fecha inicio del rango a verificar
     * @return true si existe algún período que se solape, false en caso contrario
     */
    boolean existsByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            LocalDate fechaFin, LocalDate fechaInicio);
}