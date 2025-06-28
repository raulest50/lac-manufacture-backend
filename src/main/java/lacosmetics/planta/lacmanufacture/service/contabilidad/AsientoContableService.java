package lacosmetics.planta.lacmanufacture.service.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import lacosmetics.planta.lacmanufacture.model.contabilidad.LineaAsientoContable;
import lacosmetics.planta.lacmanufacture.model.contabilidad.PeriodoContable;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.AsientoContableRepo;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.CuentaContableRepo;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.PeriodoContableRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar los asientos contables.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsientoContableService {
    
    private final AsientoContableRepo asientoContableRepo;
    private final CuentaContableRepo cuentaContableRepo;
    private final PeriodoContableRepo periodoContableRepo;
    
    /**
     * Obtiene todos los asientos contables, opcionalmente filtrados por período y estado.
     * 
     * @param periodoId ID del período contable (opcional)
     * @param estado Estado del asiento (opcional)
     * @return Lista de asientos contables que cumplen con los criterios
     */
    public List<AsientoContable> obtenerTodosLosAsientos(Long periodoId, AsientoContable.EstadoAsiento estado) {
        log.info("Obteniendo asientos contables. Período: {}, Estado: {}", periodoId, estado);
        
        // Por simplicidad, retornamos todos los asientos y dejamos para una implementación
        // futura la adición de filtros más específicos
        return asientoContableRepo.findAll();
    }
    
    /**
     * Obtiene un asiento contable por su ID.
     * 
     * @param id ID del asiento contable
     * @return Optional con el asiento contable si existe, vacío en caso contrario
     */
    public Optional<AsientoContable> obtenerAsientoPorId(Long id) {
        log.info("Buscando asiento contable con ID: {}", id);
        return asientoContableRepo.findById(id);
    }
    
    /**
     * Crea un nuevo asiento contable.
     * 
     * @param asiento El asiento contable a crear
     * @return El asiento contable creado
     * @throws RuntimeException si hay errores de validación
     */
    @Transactional
    public AsientoContable crearAsiento(AsientoContable asiento) {
        log.info("Creando nuevo asiento contable");
        
        // Validar que el período exista y esté abierto
        if (asiento.getPeriodoContable() != null && asiento.getPeriodoContable().getId() != null) {
            PeriodoContable periodo = periodoContableRepo.findById(asiento.getPeriodoContable().getId())
                    .orElseThrow(() -> {
                        log.error("Error al crear asiento: el período contable con ID {} no existe", 
                                asiento.getPeriodoContable().getId());
                        return new RuntimeException("El período contable no existe");
                    });
            
            if (periodo.getEstado() == PeriodoContable.EstadoPeriodo.CERRADO) {
                log.error("Error al crear asiento: el período contable está cerrado");
                throw new RuntimeException("No se puede crear un asiento en un período cerrado");
            }
            
            asiento.setPeriodoContable(periodo);
        }
        
        // Validar que las cuentas existan
        for (LineaAsientoContable linea : asiento.getLineas()) {
            if (!cuentaContableRepo.existsById(linea.getCuentaCodigo())) {
                log.error("Error al crear asiento: la cuenta {} no existe", linea.getCuentaCodigo());
                throw new RuntimeException("La cuenta " + linea.getCuentaCodigo() + " no existe");
            }
            linea.setAsientoContable(asiento);
        }
        
        // Validar que el asiento esté balanceado
        validarCuadreContable(asiento);
        
        return asientoContableRepo.save(asiento);
    }
    
    /**
     * Actualiza un asiento contable existente.
     * 
     * @param id ID del asiento a actualizar
     * @param asientoActualizado Datos actualizados del asiento
     * @return El asiento contable actualizado
     * @throws RuntimeException si no existe un asiento con el ID especificado o hay errores de validación
     */
    @Transactional
    public AsientoContable actualizarAsiento(Long id, AsientoContable asientoActualizado) {
        log.info("Actualizando asiento contable con ID: {}", id);
        
        AsientoContable asientoExistente = asientoContableRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar asiento: no existe un asiento con el ID {}", id);
                    return new RuntimeException("No existe un asiento con el ID " + id);
                });
        
        // Validar que el asiento no esté publicado
        if (asientoExistente.getEstado() == AsientoContable.EstadoAsiento.PUBLICADO) {
            log.error("Error al actualizar asiento: el asiento está en estado PUBLICADO");
            throw new RuntimeException("No se puede modificar un asiento en estado PUBLICADO");
        }
        
        // Actualizar propiedades básicas
        asientoExistente.setFecha(asientoActualizado.getFecha());
        asientoExistente.setDescripcion(asientoActualizado.getDescripcion());
        asientoExistente.setModulo(asientoActualizado.getModulo());
        asientoExistente.setDocumentoOrigen(asientoActualizado.getDocumentoOrigen());
        
        // Actualizar período si es necesario
        if (asientoActualizado.getPeriodoContable() != null && asientoActualizado.getPeriodoContable().getId() != null) {
            PeriodoContable periodo = periodoContableRepo.findById(asientoActualizado.getPeriodoContable().getId())
                    .orElseThrow(() -> {
                        log.error("Error al actualizar asiento: el período contable con ID {} no existe", 
                                asientoActualizado.getPeriodoContable().getId());
                        return new RuntimeException("El período contable no existe");
                    });
            
            if (periodo.getEstado() == PeriodoContable.EstadoPeriodo.CERRADO) {
                log.error("Error al actualizar asiento: el período contable está cerrado");
                throw new RuntimeException("No se puede asignar un asiento a un período cerrado");
            }
            
            asientoExistente.setPeriodoContable(periodo);
        }
        
        // Actualizar líneas
        List<LineaAsientoContable> nuevasLineas = new ArrayList<>();
        for (LineaAsientoContable linea : asientoActualizado.getLineas()) {
            if (!cuentaContableRepo.existsById(linea.getCuentaCodigo())) {
                log.error("Error al actualizar asiento: la cuenta {} no existe", linea.getCuentaCodigo());
                throw new RuntimeException("La cuenta " + linea.getCuentaCodigo() + " no existe");
            }
            
            LineaAsientoContable nuevaLinea = new LineaAsientoContable();
            nuevaLinea.setAsientoContable(asientoExistente);
            nuevaLinea.setCuentaCodigo(linea.getCuentaCodigo());
            nuevaLinea.setDebito(linea.getDebito());
            nuevaLinea.setCredito(linea.getCredito());
            nuevaLinea.setDescripcion(linea.getDescripcion());
            
            nuevasLineas.add(nuevaLinea);
        }
        
        // Reemplazar las líneas existentes con las nuevas
        asientoExistente.getLineas().clear();
        asientoExistente.getLineas().addAll(nuevasLineas);
        
        // Validar que el asiento esté balanceado
        validarCuadreContable(asientoExistente);
        
        return asientoContableRepo.save(asientoExistente);
    }
    
    /**
     * Cambia el estado de un asiento contable.
     * 
     * @param id ID del asiento
     * @param nuevoEstado Nuevo estado del asiento
     * @return El asiento contable actualizado
     * @throws RuntimeException si no existe un asiento con el ID especificado o hay errores de validación
     */
    @Transactional
    public AsientoContable cambiarEstadoAsiento(Long id, AsientoContable.EstadoAsiento nuevoEstado) {
        log.info("Cambiando estado de asiento contable con ID: {} a {}", id, nuevoEstado);
        
        AsientoContable asiento = asientoContableRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al cambiar estado: no existe un asiento con el ID {}", id);
                    return new RuntimeException("No existe un asiento con el ID " + id);
                });
        
        // Validar transiciones de estado
        if (asiento.getEstado() == AsientoContable.EstadoAsiento.PUBLICADO && 
            nuevoEstado == AsientoContable.EstadoAsiento.BORRADOR) {
            log.error("Error al cambiar estado: no se puede cambiar de PUBLICADO a BORRADOR");
            throw new RuntimeException("No se puede cambiar un asiento PUBLICADO a BORRADOR");
        }
        
        if (nuevoEstado == AsientoContable.EstadoAsiento.PUBLICADO) {
            // Validar que el asiento esté balanceado antes de publicar
            validarCuadreContable(asiento);
            
            // Validar que el período esté abierto
            if (asiento.getPeriodoContable() != null && 
                asiento.getPeriodoContable().getEstado() == PeriodoContable.EstadoPeriodo.CERRADO) {
                log.error("Error al publicar asiento: el período contable está cerrado");
                throw new RuntimeException("No se puede publicar un asiento en un período cerrado");
            }
        }
        
        asiento.setEstado(nuevoEstado);
        return asientoContableRepo.save(asiento);
    }
    
    /**
     * Valida que un asiento contable esté balanceado (débitos = créditos).
     * 
     * @param asiento El asiento contable a validar
     * @throws RuntimeException si el asiento no está balanceado
     */
    private void validarCuadreContable(AsientoContable asiento) {
        BigDecimal totalDebitos = asiento.getLineas().stream()
                .map(LineaAsientoContable::getDebito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCreditos = asiento.getLineas().stream()
                .map(LineaAsientoContable::getCredito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalDebitos.compareTo(totalCreditos) != 0) {
            log.error("Error de validación: el asiento no está balanceado. Débitos={}, Créditos={}", 
                    totalDebitos, totalCreditos);
            throw new RuntimeException(
                    "El asiento contable no está balanceado: " +
                            "Débitos=" + totalDebitos + ", Créditos=" + totalCreditos
            );
        }
    }
}