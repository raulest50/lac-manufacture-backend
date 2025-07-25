package lacosmetics.planta.lacmanufacture.resource.activos.fijos;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.ItemOrdenCompraActivo;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.OrdenCompraActivo;
import lacosmetics.planta.lacmanufacture.service.activos.fijos.ActivoFijoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de activos fijos.
 * Proporciona endpoints para administrar órdenes de compra, incorporaciones,
 * depreciaciones y otros aspectos relacionados con los activos fijos.
 */
@RestController
@RequestMapping("/api/activos-fijos")
@RequiredArgsConstructor
@Slf4j
public class ActivoFijoResource {

    private final ActivoFijoService activoFijoService;

    /**
     * Endpoint para guardar una nueva orden de compra de activos fijos.
     *
     * @param ordenCompraActivo la orden de compra a guardar
     * @return la orden de compra guardada con su ID asignado
     */
    @PostMapping("/save_ocaf")
    public ResponseEntity<OrdenCompraActivo> saveOrdenCompraActivo(@RequestBody OrdenCompraActivo ordenCompraActivo) {
        log.info("REST request para guardar orden de compra de activos fijos");
        try {
            OrdenCompraActivo savedOrden = activoFijoService.saveOrdenCompraActivo(ordenCompraActivo);
            return ResponseEntity.created(URI.create("/api/activos-fijos/ordenes-compra/" + savedOrden.getOrdenCompraActivoId()))
                    .body(savedOrden);
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
        Page<OrdenCompraActivo> ordenes = activoFijoService.getOrdenesCompraByDateAndEstado(
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
            OrdenCompraActivo orden = activoFijoService.getOrdenCompraByIdAndEstado(ordenCompraActivoId, estado);
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
            OrdenCompraActivo updated = activoFijoService.cancelOrdenCompraActivo(ordenCompraActivoId);
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
            List<ItemOrdenCompraActivo> items = activoFijoService.getItemsByOrdenCompraId(ordenCompraActivoId);
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
     * @param ordenCompraActivo datos actualizados de la orden de compra
     * @return la orden de compra actualizada
     */
    @PutMapping("/ocaf/{ordenCompraActivoId}/update")
    public ResponseEntity<?> updateOrdenCompraActivo(
            @PathVariable int ordenCompraActivoId,
            @RequestBody OrdenCompraActivo ordenCompraActivo) {

        log.info("REST request para actualizar orden de compra de activos fijos con ID: {}", ordenCompraActivoId);

        try {
            // Verificar que el ID en la URL coincida con el ID en el cuerpo
            if (ordenCompraActivo.getOrdenCompraActivoId() != 0 && 
                ordenCompraActivo.getOrdenCompraActivoId() != ordenCompraActivoId) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "El ID de la orden en la URL no coincide con el ID en el cuerpo de la solicitud")
                );
            }

            // Establecer el ID desde la URL
            ordenCompraActivo.setOrdenCompraActivoId(ordenCompraActivoId);

            // Llamar al servicio para actualizar
            OrdenCompraActivo updated = activoFijoService.updateOrdenCompraActivo(ordenCompraActivo);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error al actualizar orden de compra de activos fijos", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
