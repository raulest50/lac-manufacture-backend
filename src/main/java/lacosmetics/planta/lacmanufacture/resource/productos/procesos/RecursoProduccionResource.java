package lacosmetics.planta.lacmanufacture.resource.productos.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.procesos.RecursoProduccion;
import lacosmetics.planta.lacmanufacture.service.productos.procesos.RecursoProduccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/recursos-produccion")
@RequiredArgsConstructor
@Slf4j
public class RecursoProduccionResource {

    private final RecursoProduccionService recursoProduccionService;

    /**
     * Endpoint para crear un nuevo recurso de producción.
     * 
     * @param recursoProduccion El recurso de producción a crear
     * @return El recurso de producción creado con su ID asignado
     */
    @PostMapping
    public ResponseEntity<RecursoProduccion> createRecursoProduccion(@RequestBody RecursoProduccion recursoProduccion) {
        log.info("REST request para crear un nuevo recurso de producción");
        RecursoProduccion result = recursoProduccionService.saveRecursoProduccion(recursoProduccion);
        return ResponseEntity
                .created(URI.create("/api/recursos-produccion/" + result.getId()))
                .body(result);
    }
}
