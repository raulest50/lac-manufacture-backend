package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.Movimiento;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.service.MovimientosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

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

    // New endpoint to get movimientos for a product
    @GetMapping("/get_movimientos_by_producto")
    public ResponseEntity<Page<Movimiento>> getMovimientosByProducto(
            @RequestParam int productoId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<Movimiento> movimientos = movimientoService.getMovimientosByProductoId(productoId, page, size);
        return ResponseEntity.ok().body(movimientos);
    }

}
