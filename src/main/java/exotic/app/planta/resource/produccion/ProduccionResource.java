package exotic.app.planta.resource.produccion;


import exotic.app.planta.model.produccion.OrdenProduccion;
import exotic.app.planta.model.produccion.dto.InventarioEnTransitoDTO;
import exotic.app.planta.model.produccion.dto.InsumoDTO;
import exotic.app.planta.model.produccion.dto.ODP_Data4PDF;
import exotic.app.planta.model.produccion.dto.OrdenProduccionDTO;
import exotic.app.planta.model.produccion.dto.OrdenProduccionDTO_save;
import exotic.app.planta.model.produccion.dto.OrdenSeguimientoDTO;
import exotic.app.planta.service.produccion.ProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produccion")
@RequiredArgsConstructor
public class ProduccionResource {

    private final ProduccionService produccionService;

    @GetMapping("/orden_produccion/{id}/insumos")
    public ResponseEntity<List<InsumoDTO>> getInsumosOrdenProduccion(@PathVariable int id) {
        List<InsumoDTO> insumos = produccionService.getInsumosOrdenProduccion(id);
        return ResponseEntity.ok(insumos);
    }


    @PostMapping("/save")
    public ResponseEntity<OrdenProduccion> saveOrdenProduccion(@RequestBody OrdenProduccionDTO_save ordenProduccionDTO){
        return ResponseEntity.created(URI.create("/ordenes/ordenID")).body(produccionService.saveOrdenProduccion(ordenProduccionDTO));
    }


    @GetMapping("/search_within_range")
    public ResponseEntity<Page<OrdenProduccionDTO>> searchOrdenesProduccion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam int estadoOrden,
            @RequestParam(required = false) String productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").ascending());
        Page<OrdenProduccionDTO> resultados = produccionService.searchOrdenesProduccionByDateRangeAndEstadoOrden(
                startDate,
                endDate,
                estadoOrden,
                productoId,
                pageable
        );
        return ResponseEntity.ok(resultados);
    }


    @GetMapping("/inventario_en_transito")
    public ResponseEntity<Page<InventarioEnTransitoDTO>> getInventarioEnTransito(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventarioEnTransitoDTO> inventarioEnTransito = produccionService.getInventarioEnTransito(pageable);
        return ResponseEntity.ok(inventarioEnTransito);
    }


    /**
     * Update estado of OrdenSeguimiento.
     */
    @PutMapping("/orden_seguimiento/{id}/update_estado")
    public ResponseEntity<OrdenSeguimientoDTO> updateEstadoOrdenSeguimiento(
            @PathVariable int id,
            @RequestParam int estado
    ) {
        OrdenSeguimientoDTO updatedSeguimiento = produccionService.updateEstadoOrdenSeguimiento(id, estado);
        return ResponseEntity.ok(updatedSeguimiento);
    }

    /**
     * Update estadoOrden of OrdenProduccion.
     */
    @PutMapping("/orden_produccion/{id}/update_estado")
    public ResponseEntity<OrdenProduccionDTO> updateEstadoOrdenProduccion(
            @PathVariable int id,
            @RequestParam int estadoOrden
    ) {
        OrdenProduccionDTO updatedOrden = produccionService.updateEstadoOrdenProduccion(id, estadoOrden);
        return ResponseEntity.ok(updatedOrden);
    }

    @GetMapping("/orden_produccion/{id}/is_deletable")
    public ResponseEntity<?> isOrdenProduccionCancelable(@PathVariable int id) {
        try {
            boolean cancelable = produccionService.isOrdenProduccionCancelable(id);
            return ResponseEntity.ok(Map.of("cancelable", cancelable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cancela una orden de producción siempre que esté en estado abierto (0).
     */
    @PutMapping("/orden_produccion/{id}/cancel")
    public ResponseEntity<?> cancelOrdenProduccion(@PathVariable int id) {
        try {
            OrdenProduccionDTO ordenCancelada = produccionService.cancelarOrdenProduccion(id);
            return ResponseEntity.ok(ordenCancelada);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    /**
     * Obtiene los datos necesarios para generar un PDF de un producto terminado.
     * 
     * @param id ID del producto terminado
     * @return Objeto ODP_Data4PDF con la información necesaria
     */
    @GetMapping("/terminado/{id}/data4pdf")
    public ResponseEntity<ODP_Data4PDF> getTerminadoData4PDF(@PathVariable String id) {
        ODP_Data4PDF data = produccionService.getTerminadoData4PDF(id);
        return ResponseEntity.ok(data);
    }

    /**
     * Obtiene todas las órdenes de producción en estado abierto (0) o en curso (1)
     * utilizando paginación.
     * 
     * @param page Número de página (por defecto 0)
     * @param size Tamaño de página (por defecto 10)
     * @return Página de DTOs de órdenes de producción
     */
    @GetMapping("/dispensacion_odp_consulta")
    public ResponseEntity<Page<OrdenProduccionDTO>> getOrdenesProduccionOpenOrInProgress(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        Page<OrdenProduccionDTO> resultados = produccionService.getOrdenesProduccionOpenOrInProgress(pageable);
        return ResponseEntity.ok(resultados);
    }

}
