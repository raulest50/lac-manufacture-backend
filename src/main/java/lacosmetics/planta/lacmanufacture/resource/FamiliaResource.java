package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.model.producto.Familia;
import lacosmetics.planta.lacmanufacture.service.FamiliaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/familias")
@RequiredArgsConstructor
@Slf4j
public class FamiliaResource {

    private final FamiliaService familiaService;

    /**
     * Endpoint para guardar una nueva familia o actualizar una existente
     * Verifica que el ID y nombre sean únicos antes de guardar
     * @param familia La familia a guardar
     * @return La familia guardada o un mensaje de error si no se pudo guardar
     */
    @PostMapping
    public ResponseEntity<?> saveFamilia(@RequestBody Familia familia) {
        try {
            // Validaciones básicas
            if (familia.getFamiliaNombre() == null || familia.getFamiliaNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El nombre de la familia no puede estar vacío"
                ));
            }

            Familia savedFamilia = familiaService.saveFamilia(familia);
            return ResponseEntity
                    .created(URI.create("/familias/" + savedFamilia.getFamiliaId()))
                    .body(savedFamilia);
        } catch (IllegalArgumentException e) {
            // Capturar excepciones de validación (ID o nombre duplicado)
            log.warn("Error de validación al guardar familia: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            // Capturar otras excepciones inesperadas
            log.error("Error al guardar familia: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al guardar familia: " + e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para obtener todas las familias registradas
     * @return Lista de todas las familias
     */
    @GetMapping
    public ResponseEntity<List<Familia>> getAllFamilias() {
        List<Familia> familias = familiaService.getAllFamilias();
        return ResponseEntity.ok(familias);
    }

    /**
     * Endpoint para eliminar una familia por su ID
     * Solo se puede eliminar si no está siendo referenciada por ningún producto terminado
     * @param familiaId ID de la familia a eliminar
     * @return Mensaje de éxito o error
     */
    @DeleteMapping("/{familiaId}")
    public ResponseEntity<Map<String, Object>> deleteFamilia(@PathVariable int familiaId) {
        try {
            boolean deleted = familiaService.deleteFamiliaById(familiaId);

            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Familia eliminada exitosamente"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", "No se puede eliminar la familia porque está siendo utilizada por uno o más productos terminados"
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al eliminar familia: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al eliminar familia: " + e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para obtener una familia por su ID
     * @param familiaId ID de la familia
     * @return La familia encontrada o 404 si no existe
     */
    @GetMapping("/{familiaId}")
    public ResponseEntity<?> getFamiliaById(@PathVariable int familiaId) {
        Optional<Familia> familiaOpt = familiaService.getFamiliaById(familiaId);

        if (familiaOpt.isPresent()) {
            return ResponseEntity.ok(familiaOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No se encontró familia con ID: " + familiaId));
        }
    }
}
