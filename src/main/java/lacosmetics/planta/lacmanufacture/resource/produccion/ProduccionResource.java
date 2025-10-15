package lacosmetics.planta.lacmanufacture.resource.produccion;


import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.dto.InventarioEnTransitoDTO;
import lacosmetics.planta.lacmanufacture.model.dto.InsumoDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO_save;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenSeguimientoDTO;
import lacosmetics.planta.lacmanufacture.service.produccion.ODPPdfGenerator;
import lacosmetics.planta.lacmanufacture.service.produccion.ProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/produccion")
@RequiredArgsConstructor
public class ProduccionResource {

    private final ProduccionService produccionService;
    private final ODPPdfGenerator odpPdfGenerator;

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





    @GetMapping("/orden_produccion/{id}/pdf")
    public ResponseEntity<byte[]> downloadOrdenProduccionPdf(@PathVariable int id) {
        try {
            byte[] pdfBytes = odpPdfGenerator.generateOrdenProduccionPdf(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "orden-produccion-" + id + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
