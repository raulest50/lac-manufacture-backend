package lacosmetics.planta.lacmanufacture.resource.inventarios;


import lacosmetics.planta.lacmanufacture.model.inventarios.dto.AjusteInventarioDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.BackflushNoPlanificadoDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.BackflushMultipleNoPlanificadoDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.DispensacionDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.DispensacionNoPlanificadaDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.IngresoOCM_DTA;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.LoteDisponibleResponseDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.RecomendacionLotesRequestDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.RecomendacionLotesMultipleRequestDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.MovimientoExcelRequestDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.producto.dto.ProductoStockDTO;
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

    /**
     * Endpoint para buscar productos con stock disponible.
     * Permite filtrar productos según un término de búsqueda y tipo de búsqueda específico.
     * 
     * @param searchTerm Término de búsqueda para filtrar productos
     * @param tipoBusqueda Tipo de búsqueda (por nombre, código, etc.)
     * @param page Número de página para paginación (base 0)
     * @param size Tamaño de página para paginación
     * @return Página de productos con información de stock
     */
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

    /**
     * Endpoint para exportar movimientos de inventario a un archivo Excel.
     * Genera un reporte de movimientos según los criterios especificados en el DTO.
     * 
     * @param dto DTO con los parámetros para filtrar los movimientos a exportar
     * @return Archivo Excel con los movimientos filtrados
     */
    @PostMapping("/exportar-movimientos-excel")
    public ResponseEntity<byte[]> exportMovimientosExcel(@RequestBody MovimientoExcelRequestDTO dto) {
        byte[] excel = movimientoService.generateMovimientosExcel(dto);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"movimientos.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    /**
     * Endpoint para obtener los movimientos de inventario de un producto específico.
     * Devuelve una lista paginada de movimientos ordenados por fecha descendente.
     * 
     * @param productoId ID del producto para filtrar los movimientos
     * @param page Número de página para paginación (base 0)
     * @param size Tamaño de página para paginación
     * @return Página de movimientos del producto especificado
     */
    @GetMapping("/get_movimientos_by_producto")
    public ResponseEntity<Page<Movimiento>> getMovimientosByProducto(
            @RequestParam String productoId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<Movimiento> movimientos = movimientoService.getMovimientosByProductoId(productoId, page, size);
        return ResponseEntity.ok().body(movimientos);
    }

    @PostMapping("/ajustes")
    public ResponseEntity<TransaccionAlmacen> createAjusteInventario(@RequestBody AjusteInventarioDTO ajusteInventarioDTO) {
        // Llamar directamente al servicio con el DTO original
        TransaccionAlmacen transaccion = movimientoService.createAjusteInventario(ajusteInventarioDTO);
        return ResponseEntity.created(java.net.URI.create("/movimientos/transaccion/" + transaccion.getTransaccionId()))
                .body(transaccion);
    }

    /**
     * Endpoint para registrar un documento de ingreso de materiales por orden de compra.
     * Permite adjuntar un archivo como soporte documental de la transacción.
     * 
     * @param docIngresoDTO DTO con la información del ingreso de materiales
     * @param file Archivo adjunto como soporte documental (factura, guía de remisión, etc.)
     * @return Respuesta con el resultado de la operación
     */
    @PostMapping(value = "/save_doc_ingreso_oc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDocIngreso(
            @RequestPart("docIngresoDTA") IngresoOCM_DTA docIngresoDTO,
            @RequestPart("file") MultipartFile file) {
        return movimientoService.createDocIngreso(docIngresoDTO, file);
    }

    /**
     * Endpoint para crear una dispensación asociada a una orden de producción.
     * Este endpoint maneja la salida de materiales del almacén para ejecutar órdenes de producción.
     * 
     * @param dispensacionDTO Datos de la dispensación a crear
     * @return La transacción de almacén creada
     */
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

    /**
     * Endpoint para obtener recomendaciones de lotes para dispensación.
     * Recibe un producto y cantidad, y devuelve los lotes recomendados para tomar.
     */
    @PostMapping("/recomendar-lotes")
    public ResponseEntity<DispensacionNoPlanificadaDTO> recomendarLotes(
            @RequestBody RecomendacionLotesRequestDTO requestDTO) {
        DispensacionNoPlanificadaDTO recomendacion = movimientoService.recomendarLotesParaDispensacion(
                requestDTO.getProductoId(), requestDTO.getCantidad());
        return ResponseEntity.ok(recomendacion);
    }

    /**
     * Endpoint para obtener recomendaciones de lotes para múltiples productos.
     * Recibe una lista de productos y cantidades, y devuelve los lotes recomendados para todos ellos.
     */
    @PostMapping("/recomendar-lotes-multiple")
    public ResponseEntity<DispensacionNoPlanificadaDTO> recomendarLotesMultiple(
            @RequestBody RecomendacionLotesMultipleRequestDTO requestDTO) {
        DispensacionNoPlanificadaDTO recomendacion = movimientoService.recomendarLotesParaDispensacionMultiple(
                requestDTO.getItems());
        return ResponseEntity.ok(recomendacion);
    }

    /**
     * Endpoint para crear múltiples backflush no planificados (sin orden de producción).
     * Verifica la directiva "Permitir Backflush No Planificado" antes de permitir la operación.
     * Permite especificar lotes para cada producto terminado.
     */
    @PostMapping("/backflush-multiple-no-planificado")
    public ResponseEntity<?> createBackflushMultipleNoPlanificado(
            @RequestBody BackflushMultipleNoPlanificadoDTO backflushDTO) {
        TransaccionAlmacen transaccion = movimientoService.createBackflushMultipleNoPlanificado(backflushDTO);
        return ResponseEntity.created(java.net.URI.create("/movimientos/transaccion/" + transaccion.getTransaccionId()))
            .body(transaccion);
    }

    /**
     * Endpoint para obtener los lotes disponibles de un producto específico.
     * Devuelve información detallada de cada lote, incluyendo fecha de vencimiento y cantidad disponible.
     * 
     * @param productoId ID del producto para consultar sus lotes
     * @return Información de lotes disponibles con sus cantidades
     */
    @GetMapping("/lotes-disponibles")
    public ResponseEntity<LoteDisponibleResponseDTO> getLotesDisponibles(
            @RequestParam String productoId) {
        LoteDisponibleResponseDTO lotesDisponibles = movimientoService.getLotesDisponiblesByProductoId(productoId);
        return ResponseEntity.ok(lotesDisponibles);
    }



}
