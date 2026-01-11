package exotic.app.planta.service.productos.procesos;

import exotic.app.planta.model.producto.manufacturing.procesos.ProcesoProduccionCompleto;
import exotic.app.planta.repo.producto.procesos.ProcesoProduccionCompletoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcesoProduccionCompletoService {

    private final ProcesoProduccionCompletoRepo procesoProduccionCompletoRepo;

    /**
     * Guarda un proceso de producción completo en la base de datos.
     * 
     * @param procesoProduccionCompleto El proceso de producción completo a guardar
     * @return El proceso de producción completo guardado con su ID asignado
     */
    @Transactional
    public ProcesoProduccionCompleto saveProcesoProduccionCompleto(ProcesoProduccionCompleto procesoProduccionCompleto) {
        log.info("Guardando proceso de producción completo para producto: {}", 
                procesoProduccionCompleto.getProducto() != null ? 
                procesoProduccionCompleto.getProducto().getProductoId() : "No especificado");
        
        // Aquí podríamos agregar validaciones adicionales si fueran necesarias
        
        return procesoProduccionCompletoRepo.save(procesoProduccionCompleto);
    }

    /**
     * Obtiene un proceso de producción completo por su ID.
     * 
     * @param id El ID del proceso de producción completo
     * @return Un Optional que contiene el proceso de producción completo si existe
     */
    @Transactional(readOnly = true)
    public Optional<ProcesoProduccionCompleto> getProcesoProduccionCompletoById(Integer id) {
        log.info("Buscando proceso de producción completo con ID: {}", id);
        return procesoProduccionCompletoRepo.findById(id);
    }
}