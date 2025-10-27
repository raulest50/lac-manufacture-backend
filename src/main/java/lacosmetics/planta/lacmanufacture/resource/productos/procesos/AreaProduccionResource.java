package lacosmetics.planta.lacmanufacture.resource.productos.procesos;

import jakarta.validation.Valid;
import lacosmetics.planta.lacmanufacture.dto.AreaProduccionDTO;
import lacosmetics.planta.lacmanufacture.dto.ErrorResponse;
import lacosmetics.planta.lacmanufacture.dto.SearchAreaProduccionDTO;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.AreaProduccion;
import lacosmetics.planta.lacmanufacture.service.productos.procesos.AreaProduccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/areas-produccion")
@RequiredArgsConstructor
@Slf4j
public class AreaProduccionResource {

    private final AreaProduccionService areaProduccionService;

    /**
     * Endpoint para crear un área de producción
     * 
     * @param areaProduccionDTO DTO con la información del área
     * @return ResponseEntity con el área creada o error
     */
    @PostMapping("/crear")
    public ResponseEntity<?> createAreaProduccion(@Valid @RequestBody AreaProduccionDTO areaProduccionDTO) {
        log.info("REST request para crear una nueva área de producción: {}", areaProduccionDTO.getNombre());

        try {
            AreaProduccion result = areaProduccionService.createAreaProduccionFromDTO(areaProduccionDTO);
            return ResponseEntity
                    .created(URI.create("/api/areas-produccion/" + result.getAreaId()))
                    .body(result);
        } catch (IllegalArgumentException e) {
            log.error("Error al crear área de producción: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Error al crear área", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al crear área de producción", e);
            return ResponseEntity
                    .internalServerError()
                    .body(new ErrorResponse("Error interno del servidor", "Ocurrió un error inesperado"));
        }
    }

    /**
     * Endpoint para buscar áreas de producción por nombre
     * 
     * @param searchDTO DTO con el criterio de búsqueda (nombre)
     * @param page Número de página (por defecto 0)
     * @param size Tamaño de página (por defecto 10)
     * @return ResponseEntity con la lista de áreas que coinciden con el criterio
     */
    @PostMapping("/search_by_name")
    public ResponseEntity<List<AreaProduccion>> searchAreasByName(
            @RequestBody SearchAreaProduccionDTO searchDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request para buscar áreas de producción por nombre: {}", searchDTO.getNombre());

        try {
            // Crear objeto PageRequest para paginación
            PageRequest pageable = PageRequest.of(page, size);

            // Realizar la búsqueda
            List<AreaProduccion> areas = areaProduccionService.searchAreasByName(searchDTO, pageable);

            return ResponseEntity.ok(areas);
        } catch (Exception e) {
            log.error("Error al buscar áreas de producción", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
