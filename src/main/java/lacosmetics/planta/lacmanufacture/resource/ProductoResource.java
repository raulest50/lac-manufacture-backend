package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.Producto;
import lacosmetics.planta.lacmanufacture.model.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.Terminado;
import lacosmetics.planta.lacmanufacture.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoResource {

    private final ProductoService productoService;

    @PostMapping("/save")
    public ResponseEntity<Producto> saveProducto(@RequestBody Producto producto){
        return ResponseEntity.created(URI.create("/productos/productoID")).body(productoService.saveProducto(producto));
    }

    @GetMapping("/getall")
    public ResponseEntity<Page<Producto>> getAllProductos(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        return ResponseEntity.ok().body(productoService.getAllProductos(page, size));
    }

    @GetMapping("/getall_mprima")
    public ResponseEntity<Page<MateriaPrima>> getAllMprima(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        return ResponseEntity.ok().body(productoService.getAllMP(page, size));
    }

    @GetMapping("/getall_semi")
    public ResponseEntity<Page<SemiTerminado>> getAllSemi(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        return ResponseEntity.ok().body(productoService.getAllS(page, size));
    }

    @GetMapping("/getall_termi")
    public ResponseEntity<Page<Terminado>> getAllTermi(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        return ResponseEntity.ok().body(productoService.getAllT(page, size));
    }

}
