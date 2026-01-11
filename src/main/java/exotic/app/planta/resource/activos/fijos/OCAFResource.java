package exotic.app.planta.resource.activos.fijos;

import com.fasterxml.jackson.databind.ObjectMapper;
import exotic.app.planta.model.activos.fijos.compras.ItemOrdenCompraActivo;
import exotic.app.planta.model.activos.fijos.compras.OrdenCompraActivo;
import exotic.app.planta.model.activos.fijos.dto.UpdateEstadoOrdenCompraAFRequest;
import exotic.app.planta.service.activos.fijos.OCAFService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de órdenes de compra de activos fijos (OCAF).
 * Proporciona endpoints para crear, actualizar, buscar y procesar órdenes de compra.
 */
@RestController
@RequestMapping("/api/activos-fijos")
@RequiredArgsConstructor
@Slf4j
public class OCAFResource {

    private final OCAFService ocafService;
    private final ObjectMapper objectMapper;

    /**
     * Endpoint para guardar una nueva orden de compra de activos fijos.
     *
     * @param ordenCompraActivoJson JSON con los datos de la orden de compra
     * @param cotizacionFile archivo de cotización opcional
     * @return la orden de compra guardada con su ID asignado
     */
    @PostMapping(value = "/save_ocaf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrdenCompraActivo> saveOrdenCompraActivo(
            @RequestPart("ordenCompraActivo") String ordenCompraActivoJson,
            @RequestPart(value = "cotizacionFile", required = false) MultipartFile cotizacionFile
    ) {
        log.info("REST request para guardar orden de compra de activos fijos");
        try {
            // Convert JSON string to OrdenCompraActivo object
            OrdenCompraActivo ordenCompraActivo = objectMapper.readValue(ordenCompraActivoJson, OrdenCompraActivo.class);

            // Save the orden compra activo with the optional file
            OrdenCompraActivo savedOrden = ocafService.saveOrdenCompraActivo(ordenCompraActivo, cotizacionFile);

            return ResponseEntity.created(URI.create("/api/activos-fijos/ordenes-compra/" + savedOrden.getOrdenCompraActivoId()))
                    .body(savedOrden);
        } catch (IOException e) {
            log.error("Error al procesar el archivo de cotización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar el archivo de cotización", e);
        } catch (IllegalArgumentException e) {
            log.error("Error al guardar orden de compra de activos fijos", e);
            throw e;
        }
    }

    /**
     * Endpoint para buscar órdenes de compra por rango de fechas y estados.
     *
     * @param date1 fecha inicial en formato yyyy-MM-dd
     * @param date2 fecha final en formato yyyy-MM-dd
     * @param estados cadena de estados separados por coma (ej: "0,1,2")
     * @param page número de página (0-indexed)
     * @param size tamaño de página
     * @return página de órdenes de compra que cumplen con los criterios
     */
    @GetMapping("/ocaf/search")
    public ResponseEntity<Page<OrdenCompraActivo>> searchOrdenesCompra(
            @RequestParam String date1,
            @RequestParam String date2,
            @RequestParam String estados,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        log.info("REST request para buscar órdenes de compra por fecha y estado");
        Page<OrdenCompraActivo> ordenes = ocafService.getOrdenesCompraByDateAndEstado(
                date1, date2, estados, page, size);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Endpoint para obtener una orden de compra por su ID y estado.
     *
     * @param ordenCompraActivoId ID de la orden de compra
     * @param estado estado de la orden (opcional, por defecto 0)
     * @return la orden de compra si existe
     */
    @GetMapping("/ocaf/{ordenCompraActivoId}")
    public ResponseEntity<OrdenCompraActivo> getOrdenCompraById(
            @PathVariable Integer ordenCompraActivoId,
            @RequestParam(defaultValue = "0") int estado) {

        log.info("REST request para obtener orden de compra con ID: {}", ordenCompraActivoId);
        try {
            OrdenCompraActivo orden = ocafService.getOrdenCompraByIdAndEstado(ordenCompraActivoId, estado);
            return ResponseEntity.ok(orden);
        } catch (RuntimeException e) {
            log.error("Error al obtener orden de compra", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para cancelar una orden de compra.
     *
     * @param ordenCompraActivoId ID de la orden de compra a cancelar
     * @return la orden de compra actualizada
     */
    @PutMapping("/ocaf/{ordenCompraActivoId}/cancel")
    public ResponseEntity<?> cancelOrdenCompra(@PathVariable int ordenCompraActivoId) {
        log.info("REST request para cancelar orden de compra con ID: {}", ordenCompraActivoId);
        try {
            OrdenCompraActivo updated = ocafService.cancelOrdenCompraActivo(ordenCompraActivoId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error al cancelar orden de compra", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener los ítems de una orden de compra específica.
     *
     * @param ordenCompraActivoId ID de la orden de compra
     * @return lista de ítems de la orden
     */
    @GetMapping("/ocaf/{ordenCompraActivoId}/items")
    public ResponseEntity<?> getItemsByOrdenCompraId(@PathVariable int ordenCompraActivoId) {
        log.info("REST request para obtener ítems de orden de compra con ID: {}", ordenCompraActivoId);
        try {
            List<ItemOrdenCompraActivo> items = ocafService.getItemsByOrdenCompraId(ordenCompraActivoId);
            return ResponseEntity.ok(items);
        } catch (RuntimeException e) {
            log.error("Error al obtener ítems de orden de compra", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para actualizar una orden de compra de activos fijos existente.
     *
     * @param ordenCompraActivoId ID de la orden de compra a actualizar
     * @param ordenCompraActivoJson JSON con los datos actualizados de la orden de compra
     * @param cotizacionFile archivo de cotización opcional
     * @return la orden de compra actualizada
     */
    @PutMapping(value = "/ocaf/{ordenCompraActivoId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateOrdenCompraActivo(
            @PathVariable int ordenCompraActivoId,
            @RequestPart("ordenCompraActivo") String ordenCompraActivoJson,
            @RequestPart(value = "cotizacionFile", required = false) MultipartFile cotizacionFile) {

        log.info("REST request para actualizar orden de compra de activos fijos con ID: {}", ordenCompraActivoId);

        try {
            // Convert JSON string to OrdenCompraActivo object
            OrdenCompraActivo ordenCompraActivo = objectMapper.readValue(ordenCompraActivoJson, OrdenCompraActivo.class);

            // Verificar que el ID en la URL coincida con el ID en el cuerpo
            if (ordenCompraActivo.getOrdenCompraActivoId() != 0 && 
                ordenCompraActivo.getOrdenCompraActivoId() != ordenCompraActivoId) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "El ID de la orden en la URL no coincide con el ID en el cuerpo de la solicitud")
                );
            }

            // Establecer el ID desde la URL
            ordenCompraActivo.setOrdenCompraActivoId(ordenCompraActivoId);

            // Llamar al servicio para actualizar con el archivo opcional
            OrdenCompraActivo updated = ocafService.updateOrdenCompraActivo(ordenCompraActivo, cotizacionFile);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            log.error("Error al procesar el archivo de cotización: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Error al procesar el archivo de cotización: " + e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error al actualizar orden de compra de activos fijos", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para actualizar el estado de una orden de compra de activos fijos.
     * Permite realizar operaciones como liberación, cancelación y envío a proveedor.
     * Si el estado es 2 (enviar a proveedor), también puede enviar la orden por email
     * al proveedor con copia a usuarios con acceso nivel 2 al módulo de producción.
     *
     * @param ordenCompraActivoId ID de la orden de compra a actualizar
     * @param request objeto con la información de actualización
     * @param pdfAttachment archivo PDF opcional para adjuntar al email
     * @return la orden de compra actualizada
     */
    @PutMapping(value = "/ocaf/{ordenCompraActivoId}/updateEstado", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateEstadoOrdenCompraActivo(
            @PathVariable int ordenCompraActivoId,
            @RequestPart("request") UpdateEstadoOrdenCompraAFRequest request,
            @RequestPart(value = "OCAFpdf", required = false) MultipartFile pdfAttachment
    ) {
        log.info("REST request para actualizar estado de orden de compra de activos fijos con ID: {}", ordenCompraActivoId);
        try {
            // Si se proporciona un archivo, asignarlo al request
            if (pdfAttachment != null) {
                request.setOCAFpdf(pdfAttachment);
            }

            OrdenCompraActivo updated = ocafService.updateEstadoOrdenCompraActivo(ordenCompraActivoId, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            // Devolver un error con el mensaje específico
            log.error("Error al actualizar estado de orden de compra de activos fijos", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}