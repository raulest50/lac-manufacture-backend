package lacosmetics.planta.lacmanufacture.resource.productos.procesos;

import jakarta.validation.Valid;
import lacosmetics.planta.lacmanufacture.dto.ErrorResponse;
import lacosmetics.planta.lacmanufacture.dto.ProcesoProduccionDTO;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos.ProcesoProduccion;
import lacosmetics.planta.lacmanufacture.service.productos.procesos.ProcesoProduccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/procesos-produccion")
@RequiredArgsConstructor
@Slf4j
public class ProcesoProduccionResource {

    private final ProcesoProduccionService procesoProduccionService;

    @PostMapping
    public ResponseEntity<?> createProcesoProduccion(@Valid @RequestBody ProcesoProduccionDTO procesoProduccionDTO) {
        log.info("REST request para crear un nuevo proceso de producción: {}", procesoProduccionDTO.getNombre());

        try {
            ProcesoProduccion result = procesoProduccionService.createProcesoProduccionFromDTO(procesoProduccionDTO);
            return ResponseEntity
                    .created(URI.create("/api/procesos-produccion/" + result.getProcesoId()))
                    .body(result);
        } catch (IllegalArgumentException e) {
            log.error("Error al crear proceso de producción: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Error al crear proceso", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al crear proceso de producción", e);
            return ResponseEntity
                    .internalServerError()
                    .body(new ErrorResponse("Error interno del servidor", "Ocurrió un error inesperado"));
        }
    }

    @GetMapping("/paginados")
    public ResponseEntity<Page<ProcesoProduccion>> getProcesosProduccionPaginados(Pageable pageable) {
        log.info("REST request para obtener procesos de producción paginados");
        Page<ProcesoProduccion> result = procesoProduccionService.getProcesosProduccionPaginados(pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update_proc_produccion/{id}")
    public ResponseEntity<?> updateProcesoProduccion(
            @PathVariable Integer id,
            @Valid @RequestBody ProcesoProduccionDTO procesoProduccionDTO) {
        log.info("REST request para actualizar proceso de producción con ID: {}", id);

        // Verificar que el ID en el path coincide con el ID en el DTO (si está presente)
        if (procesoProduccionDTO.getProcesoId() != null && !procesoProduccionDTO.getProcesoId().equals(id)) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Error de validación", 
                            "El ID en la URL (" + id + ") no coincide con el ID en el cuerpo de la solicitud (" 
                            + procesoProduccionDTO.getProcesoId() + ")"));
        }

        // Verificar que el proceso existe
        if (!procesoProduccionService.getProcesoProduccionById(id).isPresent()) {
            return ResponseEntity
                    .status(404)
                    .body(new ErrorResponse("Recurso no encontrado", 
                            "No se encontró el proceso de producción con ID: " + id));
        }

        try {
            ProcesoProduccion result = procesoProduccionService.updateProcesoProduccionFromDTO(id, procesoProduccionDTO);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Error al actualizar proceso de producción: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Error al actualizar proceso", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al actualizar proceso de producción", e);
            return ResponseEntity
                    .internalServerError()
                    .body(new ErrorResponse("Error interno del servidor", "Ocurrió un error inesperado"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProcesoProduccion(@PathVariable Integer id) {
        log.info("REST request para eliminar proceso de producción con ID: {}", id);

        if (!procesoProduccionService.getProcesoProduccionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {
            procesoProduccionService.deleteProcesoProduccion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            log.warn("No se puede eliminar el proceso de producción con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("No se puede eliminar el proceso de producción", e.getMessage()));
        }
    }

    /**
     * Verifica si un proceso de producción puede ser eliminado
     * 
     * @param id El ID del proceso de producción a verificar
     * @return Respuesta con información sobre si el proceso es eliminable
     */
    @GetMapping("/is-deletable/{id}")
    public ResponseEntity<?> isProcesoProduccionDeletable(@PathVariable Integer id) {
        log.info("REST request para verificar si el proceso de producción con ID: {} es eliminable", id);

        Map<String, Object> result = procesoProduccionService.isProcesoProduccionDeletable(id);
        return ResponseEntity.ok(result);
    }
}
