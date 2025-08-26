package lacosmetics.planta.lacmanufacture.resource.inventarios;


import lacosmetics.planta.lacmanufacture.model.inventarios.dto.BackflushNoPlanificadoDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.DispensacionDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.DispensacionNoPlanificadaDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.IngresoOCM_DTA;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.MovimientoExcelRequestDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.service.inventarios.MovimientosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientosResource {

    private final MovimientosService movimientoService;

    // New endpoint to search products with stock
    @GetMapping("/search_products_with_stock")
    public ResponseEntity<Page<ProductoStockDTO>> searchProductsWithStock(
            @RequestParam String searchTerm,
            @RequestParam String tipoBusqueda,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<ProductoStockDTO> result = movimientoService.searchProductsWithStock(searchTerm, tipoBusqueda, page, size);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/exportar-movimientos-excel")
    public ResponseEntity<byte[]> exportMovimientosExcel(@RequestBody MovimientoExcelRequestDTO dto) {
        byte[] excel = movimientoService.generateMovimientosExcel(dto);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"movimientos.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    // New endpoint to get movimientos for a product
    @GetMapping("/get_movimientos_by_producto")
    public ResponseEntity<Page<Movimiento>> getMovimientosByProducto(
            @RequestParam String productoId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<Movimiento> movimientos = movimientoService.getMovimientosByProductoId(productoId, page, size);
        return ResponseEntity.ok().body(movimientos);
    }

    @PostMapping(value = "/save_doc_ingreso_oc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDocIngreso(
            @RequestPart("docIngresoDTA") IngresoOCM_DTA docIngresoDTO,
            @RequestPart("file") MultipartFile file) {
        return movimientoService.createDocIngreso(docIngresoDTO, file);
    }

    @PostMapping("/dispensacion")
    public ResponseEntity<?> createDispensacion(@RequestBody DispensacionDTO dispensacionDTO) {
        TransaccionAlmacen transaccion = movimientoService.createDispensacion(dispensacionDTO);
        return ResponseEntity.created(java.net.URI.create("/movimientos/transaccion/" + transaccion.getTransaccionId()))
            .body(transaccion);
    }

    /**
     * Endpoint para crear una dispensación no planificada (sin orden de producción).
     * Verifica la directiva "Permitir Consumo No Planificado" antes de permitir la operación.
     */
    @PostMapping("/dispensacion-no-planificada")
    public ResponseEntity<?> createDispensacionNoPlanificada(@RequestBody DispensacionNoPlanificadaDTO dispensacionDTO) {
        TransaccionAlmacen transaccion = movimientoService.createDispensacionNoPlanificada(dispensacionDTO);
        return ResponseEntity.created(java.net.URI.create("/movimientos/transaccion/" + transaccion.getTransaccionId()))
            .body(transaccion);
    }

    /**
     * Endpoint para crear un backflush no planificado (sin orden de producción).
     * Verifica la directiva "Permitir Backflush No Planificado" antes de permitir la operación.
     */
    @PostMapping("/backflush-no-planificado")
    public ResponseEntity<?> createBackflushNoPlanificado(@RequestBody BackflushNoPlanificadoDTO backflushDTO) {
        TransaccionAlmacen transaccion = movimientoService.createBackflushNoPlanificado(backflushDTO);
        return ResponseEntity.created(java.net.URI.create("/movimientos/transaccion/" + transaccion.getTransaccionId()))
            .body(transaccion);
    }



}
