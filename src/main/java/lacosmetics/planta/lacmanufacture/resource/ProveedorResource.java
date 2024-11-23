package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.Proveedor;
import lacosmetics.planta.lacmanufacture.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorResource {

    private final ProveedorService proveedorService;

    @PostMapping("/save")
    public ResponseEntity<Proveedor> saveProducto(@RequestBody Proveedor proveedor){
        return ResponseEntity.created(URI.create("/proveedor/proveedorID")).body(proveedorService.saveProveedor(proveedor));
    }


    @GetMapping("/search")
    public ResponseEntity<List<Proveedor>> searchProveedores(@RequestParam("q") String searchText) {
        List<Proveedor> proveedores = proveedorService.searchProveedores(searchText);
        return ResponseEntity.ok(proveedores);
    }

}
