package exotic.app.planta.resource.productos.procesos;

import exotic.app.planta.model.producto.manufacturing.procesos.ProcesoProduccionCompleto;
import exotic.app.planta.service.productos.procesos.ProcesoProduccionCompletoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/procesos-produccion-completos")
@RequiredArgsConstructor
@Slf4j
public class ProcesoProduccionCompletoResource {

    private final ProcesoProduccionCompletoService procesoProduccionCompletoService;

    /**
     * Endpoint para crear un nuevo proceso de producción completo.
     * 
     * @param procesoProduccionCompleto El proceso de producción completo a crear
     * @return ResponseEntity con el proceso de producción completo creado
     */
    @PostMapping
    public ResponseEntity<ProcesoProduccionCompleto> createProcesoProduccionCompleto(
            @RequestBody ProcesoProduccionCompleto procesoProduccionCompleto) {
        log.info("REST request para crear un nuevo proceso de producción completo");
        
        ProcesoProduccionCompleto result = procesoProduccionCompletoService.saveProcesoProduccionCompleto(procesoProduccionCompleto);
        
        return ResponseEntity
                .created(URI.create("/api/procesos-produccion-completos/" + result.getProcesoCompletoId()))
                .body(result);
    }
}