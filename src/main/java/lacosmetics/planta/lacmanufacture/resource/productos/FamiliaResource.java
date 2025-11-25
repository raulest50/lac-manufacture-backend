package lacosmetics.planta.lacmanufacture.resource.productos;

import lacosmetics.planta.lacmanufacture.model.producto.Familia;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.FamiliaExceptions.DuplicateIdException;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.FamiliaExceptions.DuplicateNameException;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.FamiliaExceptions.EmptyFieldException;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.FamiliaExceptions.ValidationException;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.FamiliaExceptions.ErrorResponse;
import lacosmetics.planta.lacmanufacture.service.productos.FamiliaService;
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
     * @param familia La familia a guardar
     * @return La familia guardada
     * @throws EmptyFieldException si el nombre de la familia está vacío
     * @throws DuplicateIdException si ya existe una familia con el mismo ID
     * @throws DuplicateNameException si ya existe una familia con el mismo nombre
     */
    @PostMapping
    public ResponseEntity<Familia> saveFamilia(@RequestBody Familia familia) {
        Familia savedFamilia = familiaService.saveFamilia(familia);
        return ResponseEntity
                .created(URI.create("/familias/" + savedFamilia.getFamiliaId()))
                .body(savedFamilia);
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
     * @throws ValidationException si la familia no existe
     */
    @DeleteMapping("/{familiaId}")
    public ResponseEntity<Map<String, Object>> deleteFamilia(@PathVariable int familiaId) {
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
    }

    /**
     * Manejador para excepciones de campos vacíos
     */
    @ExceptionHandler(EmptyFieldException.class)
    public ResponseEntity<ErrorResponse> handleEmptyFieldException(EmptyFieldException e) {
        log.warn("Error de campo vacío: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * Manejador para excepciones de ID duplicado
     */
    @ExceptionHandler(DuplicateIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateIdException(DuplicateIdException e) {
        log.warn("Error de ID duplicado: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * Manejador para excepciones de nombre duplicado
     */
    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateNameException(DuplicateNameException e) {
        log.warn("Error de nombre duplicado: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * Manejador para otras excepciones de validación
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        log.warn("Error de validación: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * Manejador para excepciones generales
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error al procesar la solicitud: " + e.getMessage()));
    }

    /**
     * Endpoint para obtener una familia por su ID
     * @param familiaId ID de la familia
     * @return La familia encontrada
     * @throws ValidationException si la familia no existe
     */
    @GetMapping("/{familiaId}")
    public ResponseEntity<Familia> getFamiliaById(@PathVariable int familiaId) {
        Optional<Familia> familiaOpt = familiaService.getFamiliaById(familiaId);

        if (familiaOpt.isPresent()) {
            return ResponseEntity.ok(familiaOpt.get());
        } else {
            throw new ValidationException("No se encontró familia con ID: " + familiaId);
        }
    }


}
