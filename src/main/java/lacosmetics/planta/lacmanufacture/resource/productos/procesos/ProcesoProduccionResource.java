package lacosmetics.planta.lacmanufacture.resource.productos.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoProduccion;
import lacosmetics.planta.lacmanufacture.service.productos.procesos.ProcesoProduccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/procesos-produccion")
@RequiredArgsConstructor
@Slf4j
public class ProcesoProduccionResource {

    private final ProcesoProduccionService procesoProduccionService;

    @PostMapping
    public ResponseEntity<ProcesoProduccion> createProcesoProduccion(@RequestBody ProcesoProduccion procesoProduccion) {
        log.info("REST request para crear un nuevo proceso de producción");
        ProcesoProduccion result = procesoProduccionService.saveProcesoProduccion(procesoProduccion);
        return ResponseEntity
                .created(URI.create("/api/procesos-produccion/" + result.getProcesoId()))
                .body(result);
    }

    @GetMapping("/paginados")
    public ResponseEntity<Page<ProcesoProduccion>> getProcesosProduccionPaginados(Pageable pageable) {
        log.info("REST request para obtener procesos de producción paginados");
        Page<ProcesoProduccion> result = procesoProduccionService.getProcesosProduccionPaginados(pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcesoProduccion> updateProcesoProduccion(
            @PathVariable Integer id,
            @RequestBody ProcesoProduccion procesoProduccion) {
        log.info("REST request para actualizar proceso de producción con ID: {}", id);
        if (procesoProduccion.getProcesoId() != id) {
            return ResponseEntity.badRequest().build();
        }
        
        if (!procesoProduccionService.getProcesoProduccionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        ProcesoProduccion result = procesoProduccionService.saveProcesoProduccion(procesoProduccion);
        return ResponseEntity.ok(result);
    }
}