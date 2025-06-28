package lacosmetics.planta.lacmanufacture.service.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import lacosmetics.planta.lacmanufacture.model.contabilidad.PeriodoContable;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.AsientoContableRepo;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.PeriodoContableRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar los períodos contables.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PeriodoContableService {
    
    private final PeriodoContableRepo periodoContableRepo;
    private final AsientoContableRepo asientoContableRepo;
    
    /**
     * Obtiene todos los períodos contables, opcionalmente filtrados por estado.
     * 
     * @param estado Estado del período (opcional)
     * @return Lista de períodos contables que cumplen con los criterios
     */
    public List<PeriodoContable> obtenerTodosLosPeriodos(PeriodoContable.EstadoPeriodo estado) {
        log.info("Obteniendo períodos contables. Estado: {}", estado);
        
        if (estado != null) {
            return periodoContableRepo.findByEstado(estado);
        }
        return periodoContableRepo.findAll();
    }
    
    /**
     * Obtiene un período contable por su ID.
     * 
     * @param id ID del período contable
     * @return Optional con el período contable si existe, vacío en caso contrario
     */
    public Optional<PeriodoContable> obtenerPeriodoPorId(Long id) {
        log.info("Buscando período contable con ID: {}", id);
        return periodoContableRepo.findById(id);
    }
    
    /**
     * Crea un nuevo período contable.
     * 
     * @param periodo El período contable a crear
     * @return El período contable creado
     * @throws RuntimeException si hay errores de validación
     */
    @Transactional
    public PeriodoContable crearPeriodo(PeriodoContable periodo) {
        log.info("Creando nuevo período contable: {}", periodo.getNombre());
        
        // Validar que no se solape con otros períodos
        if (periodoContableRepo.existsByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                periodo.getFechaFin(), periodo.getFechaInicio())) {
            log.error("Error al crear período: se solapa con otro período existente");
            throw new RuntimeException("El período se solapa con otro período existente");
        }
        
        return periodoContableRepo.save(periodo);
    }
    
    /**
     * Actualiza un período contable existente.
     * 
     * @param id ID del período a actualizar
     * @param periodoActualizado Datos actualizados del período
     * @return El período contable actualizado
     * @throws RuntimeException si no existe un período con el ID especificado o hay errores de validación
     */
    @Transactional
    public PeriodoContable actualizarPeriodo(Long id, PeriodoContable periodoActualizado) {
        log.info("Actualizando período contable con ID: {}", id);
        
        PeriodoContable periodoExistente = periodoContableRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar período: no existe un período con el ID {}", id);
                    return new RuntimeException("No existe un período con el ID " + id);
                });
        
        // Validar que el período no esté cerrado
        if (periodoExistente.getEstado() == PeriodoContable.EstadoPeriodo.CERRADO) {
            log.error("Error al actualizar período: el período está cerrado");
            throw new RuntimeException("No se puede modificar un período cerrado");
        }
        
        // Actualizar propiedades
        periodoExistente.setNombre(periodoActualizado.getNombre());
        
        // Si cambian las fechas, validar solapamiento
        if (!periodoExistente.getFechaInicio().equals(periodoActualizado.getFechaInicio()) ||
            !periodoExistente.getFechaFin().equals(periodoActualizado.getFechaFin())) {
            
            periodoExistente.setFechaInicio(periodoActualizado.getFechaInicio());
            periodoExistente.setFechaFin(periodoActualizado.getFechaFin());
            
            // Validar que no se solape con otros períodos
            List<PeriodoContable> periodos = periodoContableRepo.findAll();
            for (PeriodoContable p : periodos) {
                if (p.getId().equals(id)) {
                    continue; // Excluir el período actual
                }
                
                // Verificar solapamiento
                boolean seSuperponen = !(p.getFechaFin().isBefore(periodoExistente.getFechaInicio()) || 
                                        p.getFechaInicio().isAfter(periodoExistente.getFechaFin()));
                
                if (seSuperponen) {
                    log.error("Error al actualizar período: se solapa con el período {}", p.getNombre());
                    throw new RuntimeException("El período se solapa con otro período existente: " + p.getNombre());
                }
            }
        }
        
        return periodoContableRepo.save(periodoExistente);
    }
    
    /**
     * Cambia el estado de un período contable.
     * 
     * @param id ID del período
     * @param nuevoEstado Nuevo estado del período
     * @return El período contable actualizado
     * @throws RuntimeException si no existe un período con el ID especificado o hay errores de validación
     */
    @Transactional
    public PeriodoContable cambiarEstadoPeriodo(Long id, PeriodoContable.EstadoPeriodo nuevoEstado) {
        log.info("Cambiando estado de período contable con ID: {} a {}", id, nuevoEstado);
        
        PeriodoContable periodo = periodoContableRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al cambiar estado: no existe un período con el ID {}", id);
                    return new RuntimeException("No existe un período con el ID " + id);
                });
        
        if (nuevoEstado == PeriodoContable.EstadoPeriodo.CERRADO) {
            // Verificar que no haya asientos en borrador
            boolean tieneAsientosEnBorrador = false;
            
            if (periodo.getAsientos() != null) {
                tieneAsientosEnBorrador = periodo.getAsientos().stream()
                        .anyMatch(a -> a.getEstado() == AsientoContable.EstadoAsiento.BORRADOR);
            }
            
            if (tieneAsientosEnBorrador) {
                log.error("Error al cerrar período: tiene asientos en estado BORRADOR");
                throw new RuntimeException("No se puede cerrar un período con asientos en estado BORRADOR");
            }
        }
        
        periodo.setEstado(nuevoEstado);
        return periodoContableRepo.save(periodo);
    }
}