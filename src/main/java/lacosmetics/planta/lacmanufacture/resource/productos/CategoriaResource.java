package lacosmetics.planta.lacmanufacture.resource.productos;

import lacosmetics.planta.lacmanufacture.model.producto.Categoria;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.CategoriaExceptions.DuplicateIdException;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.CategoriaExceptions.DuplicateNameException;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.CategoriaExceptions.EmptyFieldException;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.CategoriaExceptions.ValidationException;
import lacosmetics.planta.lacmanufacture.resource.productos.exceptions.CategoriaExceptions.ErrorResponse;
import lacosmetics.planta.lacmanufacture.service.productos.CategoriaService;
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
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Slf4j
public class CategoriaResource {

    private final CategoriaService categoriaService;

    /**
     * Endpoint para guardar una nueva categoría o actualizar una existente
     * @param categoria La categoría a guardar
     * @return La categoría guardada
     * @throws EmptyFieldException si el nombre de la categoría está vacío
     * @throws DuplicateIdException si ya existe una categoría con el mismo ID
     * @throws DuplicateNameException si ya existe una categoría con el mismo nombre
     */
    @PostMapping
    public ResponseEntity<Categoria> saveCategoria(@RequestBody Categoria categoria) {
        Categoria savedCategoria = categoriaService.saveCategoria(categoria);
        return ResponseEntity
                .created(URI.create("/categorias/" + savedCategoria.getCategoriaId()))
                .body(savedCategoria);
    }

    /**
     * Endpoint para obtener todas las categorías registradas
     * @return Lista de todas las categorías
     */
    @GetMapping
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        List<Categoria> categorias = categoriaService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Endpoint para eliminar una categoría por su ID
     * Solo se puede eliminar si no está siendo referenciada por ningún producto terminado
     * @param categoriaId ID de la categoría a eliminar
     * @return Mensaje de éxito o error
     * @throws ValidationException si la categoría no existe
     */
    @DeleteMapping("/{categoriaId}")
    public ResponseEntity<Map<String, Object>> deleteCategoria(@PathVariable int categoriaId) {
        boolean deleted = categoriaService.deleteCategoriaById(categoriaId);

        if (deleted) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Categoría eliminada exitosamente"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "success", false,
                "message", "No se puede eliminar la categoría porque está siendo utilizada por uno o más productos terminados"
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
     * Endpoint para obtener una categoría por su ID
     * @param categoriaId ID de la categoría
     * @return La categoría encontrada
     * @throws ValidationException si la categoría no existe
     */
    @GetMapping("/{categoriaId}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable int categoriaId) {
        Optional<Categoria> categoriaOpt = categoriaService.getCategoriaById(categoriaId);

        if (categoriaOpt.isPresent()) {
            return ResponseEntity.ok(categoriaOpt.get());
        } else {
            throw new ValidationException("No se encontró categoría con ID: " + categoriaId);
        }
    }
}