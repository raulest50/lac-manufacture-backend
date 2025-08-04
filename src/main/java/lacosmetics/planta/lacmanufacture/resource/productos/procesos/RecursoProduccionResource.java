package lacosmetics.planta.lacmanufacture.resource.productos.procesos;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.dto.DTO_SearchActivoFijoDisponible;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lacosmetics.planta.lacmanufacture.model.producto.dto.procdef.ReProdModDto;
import lacosmetics.planta.lacmanufacture.model.producto.dto.search.DTO_SearchRecursoProduccion;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.RecursoProduccion;
import lacosmetics.planta.lacmanufacture.service.activos.fijos.ActivoFijoService;
import lacosmetics.planta.lacmanufacture.service.productos.procesos.RecursoProduccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/recursos-produccion")
@RequiredArgsConstructor
@Slf4j
public class RecursoProduccionResource {

    private final RecursoProduccionService recursoProduccionService;
    private final ActivoFijoService activoFijoService;

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

    /**
     * Endpoint para buscar recursos de producción según criterios.
     * Permite búsqueda por ID o por nombre (coincidencia parcial).
     * 
     * @param searchDTO DTO con los criterios de búsqueda
     * @return Página de recursos de producción que cumplen con los criterios
     */
    @PostMapping("/search")
    public ResponseEntity<Page<RecursoProduccion>> searchRecursosProduccion(
            @RequestBody DTO_SearchRecursoProduccion searchDTO) {
        log.info("REST request para buscar recursos de producción con criterios: {}", searchDTO);
        Page<RecursoProduccion> result = recursoProduccionService.searchRecursosProduccion(searchDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint para actualizar un recurso de producción existente.
     * Valida que el recurso original exista y coincida con el almacenado.
     * No permite cambiar el ID del recurso.
     * 
     * @param modDto DTO con el recurso original y el actualizado
     * @return El recurso de producción actualizado
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateRecursoProduccion(@RequestBody ReProdModDto modDto) {
        log.info("REST request para actualizar un recurso de producción");
        try {
            RecursoProduccion result = recursoProduccionService.updateRecursoProduccion(modDto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Error al actualizar recurso de producción: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para buscar activos fijos disponibles para asignar a recursos de producción.
     * Filtra por tipo PRODUCCION, estado activo y no asignados a ningún recurso.
     * 
     * @param searchDTO DTO con los criterios de búsqueda
     * @return Página de activos fijos disponibles
     */
    @PostMapping("/activos-fijos-disponibles")
    public ResponseEntity<Page<ActivoFijo>> findActivosFijosDisponibles(
            @RequestBody DTO_SearchActivoFijoDisponible searchDTO) {
        log.info("REST request para buscar activos fijos disponibles para recursos de producción");

        int page = searchDTO.getPage() != null ? searchDTO.getPage() : 0;
        int size = searchDTO.getSize() != null ? searchDTO.getSize() : 10;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        Page<ActivoFijo> result = activoFijoService.findActivosFijosDisponiblesParaProduccion(
                searchDTO.getNombreBusqueda(), pageable);

        return ResponseEntity.ok(result);
    }
}
