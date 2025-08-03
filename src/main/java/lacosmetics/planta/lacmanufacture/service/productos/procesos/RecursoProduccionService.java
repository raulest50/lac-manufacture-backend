package lacosmetics.planta.lacmanufacture.service.productos.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.procesos.RecursoProduccion;
import lacosmetics.planta.lacmanufacture.repo.producto.procesos.RecursoProduccionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class RecursoProduccionService {

    private final RecursoProduccionRepo recursoProduccionRepo;

    /**
     * Guarda un nuevo recurso de producción en la base de datos.
     * 
     * @param recursoProduccion El recurso de producción a guardar
     * @return El recurso de producción guardado con su ID asignado
     */
    @Transactional
    public RecursoProduccion saveRecursoProduccion(RecursoProduccion recursoProduccion) {
        log.info("Guardando recurso de producción: {}", recursoProduccion.getNombre());
        return recursoProduccionRepo.save(recursoProduccion);
    }
}
