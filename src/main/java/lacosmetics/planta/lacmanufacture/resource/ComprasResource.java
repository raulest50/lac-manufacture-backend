package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.model.compras.FacturaCompra;
import lacosmetics.planta.lacmanufacture.model.compras.ItemFacturaCompra;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.dto.compra.materiales.UpdateEstadoOrdenCompraRequest;
import lacosmetics.planta.lacmanufacture.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<OrdenCompraMateriales> saveOrdenCompra(@RequestBody OrdenCompraMateriales ordenCompraMateriales) {
        OrdenCompraMateriales savedOrdenCompraMateriales = compraService.saveOrdenCompra(ordenCompraMateriales);
        return ResponseEntity.created(URI.create("/compras/save_orden_compra/" + savedOrdenCompraMateriales.getOrdenCompraId()))
                .body(savedOrdenCompraMateriales);
    }


    /**
     * GET endpoint to search OrdenCompraMateriales by date range and estados.
     * Example: /compras/ordenes?date1=2025-02-01&date2=2025-02-10&estados=0,1,2&page=0&size=10
     */
    @GetMapping("/search_ordenes_by_date_estado")
    public ResponseEntity<Page<OrdenCompraMateriales>> getOrdenesCompra(
            @RequestParam String date1,
            @RequestParam String date2,
            @RequestParam String estados,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<OrdenCompraMateriales> ordenes = compraService.getOrdenesCompraByDateAndEstado(date1, date2, estados, page, size);
        return ResponseEntity.ok(ordenes);
    }

    @PutMapping("/orden_compra/{ordenCompraId}/cancel")
    public ResponseEntity<OrdenCompraMateriales> cancelOrdenCompra(@PathVariable int ordenCompraId) {
        OrdenCompraMateriales updated = compraService.cancelOrdenCompra(ordenCompraId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/orden_compra/{ordenCompraId}/updateEstado")
    public ResponseEntity<OrdenCompraMateriales> updateEstadoOrdenCompra(
            @PathVariable int ordenCompraId,
            @RequestBody UpdateEstadoOrdenCompraRequest request
    ) {
        OrdenCompraMateriales updated = compraService.updateEstadoOrdenCompra(ordenCompraId, request);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/orden_by_id")
    public ResponseEntity<OrdenCompraMateriales> getOrdenCompraByOrdenCompraId
            (@RequestParam Integer ordenCompraId, @RequestParam(defaultValue = "2") int estado) {
        try {
            OrdenCompraMateriales orden = compraService.getOrdenCompraByOrdenCompraIdAndEstado(ordenCompraId, estado);
            return ResponseEntity.ok(orden);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }



}
