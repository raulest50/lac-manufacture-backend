package lacosmetics.planta.lacmanufacture.resource.productos.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoRecurso;
import lacosmetics.planta.lacmanufacture.service.productos.procesos.ProcesoRecursoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/proceso-recursos")
@RequiredArgsConstructor
@Slf4j
public class ProcesoRecursoResource {

    private final ProcesoRecursoService procesoRecursoService;

    @PostMapping
    public ResponseEntity<ProcesoRecurso> createProcesoRecurso(@RequestBody ProcesoRecurso procesoRecurso) {
        log.info("REST request para crear una nueva relación proceso-recurso");
        ProcesoRecurso result = procesoRecursoService.saveProcesoRecurso(procesoRecurso);
        return ResponseEntity
                .created(URI.create("/api/proceso-recursos/" + result.getId()))
                .body(result);
    }

    @GetMapping
    public ResponseEntity<List<ProcesoRecurso>> getAllProcesoRecursos() {
        log.info("REST request para obtener todas las relaciones proceso-recurso");
        List<ProcesoRecurso> result = procesoRecursoService.getAllProcesoRecursos();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcesoRecurso> getProcesoRecursoById(@PathVariable Long id) {
        log.info("REST request para obtener relación proceso-recurso con ID: {}", id);
        return procesoRecursoService.getProcesoRecursoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcesoRecurso> updateProcesoRecurso(
            @PathVariable Long id,
            @RequestBody ProcesoRecurso procesoRecurso) {
        log.info("REST request para actualizar relación proceso-recurso con ID: {}", id);
        if (procesoRecurso.getId() == null || !procesoRecurso.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        
        if (!procesoRecursoService.getProcesoRecursoById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        ProcesoRecurso result = procesoRecursoService.saveProcesoRecurso(procesoRecurso);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcesoRecurso(@PathVariable Long id) {
        log.info("REST request para eliminar relación proceso-recurso con ID: {}", id);
        if (!procesoRecursoService.getProcesoRecursoById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        procesoRecursoService.deleteProcesoRecurso(id);
        return ResponseEntity.noContent().build();
    }
}