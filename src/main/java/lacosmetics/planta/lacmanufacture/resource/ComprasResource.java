package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.model.compras.Compra;
import lacosmetics.planta.lacmanufacture.model.compras.ItemCompra;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompra;
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

    @PostMapping("/save")
    public ResponseEntity<Compra> saveCompra(@RequestBody Compra compra) {
        Compra savedCompra = compraService.saveCompra(compra);
        return ResponseEntity.created(URI.create("/compras/" + savedCompra.getCompraId())).body(savedCompra);
    }

    @GetMapping("/byProveedorAndDate")
    public ResponseEntity<Page<Compra>> getComprasByProveedorAndDate(
            @RequestParam int proveedorId,
            @RequestParam String date1,
            @RequestParam String date2,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Compra> compras = compraService.getComprasByProveedorAndDate(proveedorId, date1, date2, page, size);
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/{compraId}/items")
    public ResponseEntity<List<ItemCompra>> getItemsCompra(@PathVariable int compraId) {
        List<ItemCompra> items = compraService.getItemsByCompraId(compraId);
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

}
