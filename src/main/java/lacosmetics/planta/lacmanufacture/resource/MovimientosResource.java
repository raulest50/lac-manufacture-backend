package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.Movimiento;
import lacosmetics.planta.lacmanufacture.model.notPersisted.Stock;
import lacosmetics.planta.lacmanufacture.service.MovimientosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientosResource {

    private final MovimientosService movimientoService;

    @PostMapping("/save")
    public ResponseEntity<Movimiento> saveMovimiento(@RequestBody Movimiento movimiento){
        return ResponseEntity.created(URI.create("/movimiento/movmientoID")).body(movimientoService.saveMovimiento(movimiento));
    }

    @GetMapping("/get_stock_by_id")
    public ResponseEntity<Optional<Stock>> getStockOf(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam int producto_id)
    {
        return ResponseEntity.ok().body(movimientoService.getStockOf(producto_id));
    }

}
