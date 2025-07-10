package lacosmetics.planta.lacmanufacture.resource.ventas;

import lacosmetics.planta.lacmanufacture.model.ventas.FacturaVenta;
import lacosmetics.planta.lacmanufacture.model.ventas.OrdenVenta;
import lacosmetics.planta.lacmanufacture.service.ventas.VentasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
public class VentasResource {

    private final VentasService ventasService;

    /* Ordenes */
    @PostMapping("/ordenes")
    public ResponseEntity<OrdenVenta> saveOrden(@RequestBody OrdenVenta orden) {
        OrdenVenta saved = ventasService.saveOrdenVenta(orden);
        return ResponseEntity.created(URI.create("/ventas/ordenes/" + saved.getOrdenVentaId())).body(saved);
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<OrdenVenta> getOrden(@PathVariable int id) {
        return ventasService.findOrdenVentaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ordenes")
    public ResponseEntity<Page<OrdenVenta>> searchOrdenes(
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String estado,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<OrdenVenta> result = ventasService.searchOrdenes(clienteId, estado, page, size);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/ordenes/{id}/estado")
    public ResponseEntity<OrdenVenta> updateEstadoOrden(
            @PathVariable int id,
            @RequestParam String estado
    ) {
        OrdenVenta updated = ventasService.updateEstadoOrden(id, estado);
        return ResponseEntity.ok(updated);
    }

    /* Facturas */
    @PostMapping("/facturas")
    public ResponseEntity<FacturaVenta> saveFactura(@RequestBody FacturaVenta factura) {
        FacturaVenta saved = ventasService.saveFacturaVenta(factura);
        return ResponseEntity.created(URI.create("/ventas/facturas/" + saved.getFacturaVentaId())).body(saved);
    }

    @GetMapping("/facturas/{id}")
    public ResponseEntity<FacturaVenta> getFactura(@PathVariable int id) {
        return ventasService.findFacturaVentaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/facturas")
    public ResponseEntity<Page<FacturaVenta>> searchFacturas(
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String estado,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<FacturaVenta> result = ventasService.searchFacturas(clienteId, estado, page, size);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/facturas/{id}/estado")
    public ResponseEntity<FacturaVenta> updateEstadoFactura(
            @PathVariable int id,
            @RequestParam String estado
    ) {
        FacturaVenta updated = ventasService.updateEstadoFactura(id, estado);
        return ResponseEntity.ok(updated);
    }
}
