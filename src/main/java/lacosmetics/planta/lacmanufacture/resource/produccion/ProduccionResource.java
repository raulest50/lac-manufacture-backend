package lacosmetics.planta.lacmanufacture.resource.produccion;


import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.dto.InventarioEnTransitoDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO_save;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenSeguimientoDTO;
import lacosmetics.planta.lacmanufacture.service.produccion.ProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/produccion")
@RequiredArgsConstructor
public class ProduccionResource {

    private final ProduccionService produccionService;


    @PostMapping("/save")
    public ResponseEntity<OrdenProduccion> saveOrdenProduccion(@RequestBody OrdenProduccionDTO_save ordenProduccionDTO){
        return ResponseEntity.created(URI.create("/ordenes/ordenID")).body(produccionService.saveOrdenProduccion(ordenProduccionDTO));
    }


    @GetMapping("/search_within_range")
    public ResponseEntity<Page<OrdenProduccionDTO>> searchOrdenesProduccion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam int estadoOrden,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("fechaInicio").ascending());
        Page<OrdenProduccionDTO> resultados = produccionService.searchOrdenesProduccionByDateRangeAndEstadoOrden(startDate, endDate, estadoOrden, pageable);
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



    @GetMapping("/ordenes_produccion/responsable/{responsableId}")
    public ResponseEntity<Page<OrdenProduccionDTO>> getOrdenesProduccionByResponsable(
            @PathVariable int responsableId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrdenProduccionDTO> ordenes = produccionService.getOrdenesProduccionByResponsable(responsableId, pageable);
        return ResponseEntity.ok(ordenes);
    }


}
