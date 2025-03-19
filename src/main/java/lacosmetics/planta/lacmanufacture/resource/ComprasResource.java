package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.model.compras.FacturaCompra;
import lacosmetics.planta.lacmanufacture.model.compras.ItemFacturaCompra;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompra;
import lacosmetics.planta.lacmanufacture.model.dto.UpdateEstadoOrdenCompraRequest;
import lacosmetics.planta.lacmanufacture.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class ComprasResource {

    /**
     * Compras
     */

    private final ComprasService compraService;

    @GetMapping("/byProveedorAndDate")
    public ResponseEntity<Page<FacturaCompra>> getComprasByProveedorAndDate(
            @RequestParam int proveedorId,
            @RequestParam String date1,
            @RequestParam String date2,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<FacturaCompra> compras = compraService.getComprasByProveedorAndDate(proveedorId, date1, date2, page, size);
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/{compraId}/items")
    public ResponseEntity<List<ItemFacturaCompra>> getItemsCompra(@PathVariable int compraId) {
        List<ItemFacturaCompra> items = compraService.getItemsByCompraId(compraId);
        return ResponseEntity.ok(items);
    }


    /**
     * Ordenes de Compra
     */
    @PostMapping("/save_orden_compra")
    public ResponseEntity<OrdenCompra> saveOrdenCompra(@RequestBody OrdenCompra ordenCompra) {
        OrdenCompra savedOrdenCompra = compraService.saveOrdenCompra(ordenCompra);
        return ResponseEntity.created(URI.create("/compras/save_orden_compra/" + savedOrdenCompra.getOrdenCompraId()))
                .body(savedOrdenCompra);
    }


    /**
     * GET endpoint to search OrdenCompra by date range and estados.
     * Example: /compras/ordenes?date1=2025-02-01&date2=2025-02-10&estados=0,1,2&page=0&size=10
     */
    @GetMapping("/search_ordenes_by_date_estado")
    public ResponseEntity<Page<OrdenCompra>> getOrdenesCompra(
            @RequestParam String date1,
            @RequestParam String date2,
            @RequestParam String estados,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<OrdenCompra> ordenes = compraService.getOrdenesCompraByDateAndEstado(date1, date2, estados, page, size);
        return ResponseEntity.ok(ordenes);
    }

    @PutMapping("/orden_compra/{ordenCompraId}/cancel")
    public ResponseEntity<OrdenCompra> cancelOrdenCompra(@PathVariable int ordenCompraId) {
        OrdenCompra updated = compraService.cancelOrdenCompra(ordenCompraId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/orden_compra/{ordenCompraId}/updateEstado")
    public ResponseEntity<OrdenCompra> updateEstadoOrdenCompra(
            @PathVariable int ordenCompraId,
            @RequestBody UpdateEstadoOrdenCompraRequest request
    ) {
        OrdenCompra updated = compraService.updateEstadoOrdenCompra(ordenCompraId, request);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/orden_by_id")
    public ResponseEntity<OrdenCompra> getOrdenCompraByOrdenCompraId
            (@RequestParam Integer ordenCompraId, @RequestParam(defaultValue = "2") int estado) {
        try {
            OrdenCompra orden = compraService.getOrdenCompraByOrdenCompraIdAndEstado(ordenCompraId, estado);
            return ResponseEntity.ok(orden);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }



}
