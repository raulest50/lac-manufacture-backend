package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.model.Compra;
import lacosmetics.planta.lacmanufacture.model.ItemCompra;
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

}
