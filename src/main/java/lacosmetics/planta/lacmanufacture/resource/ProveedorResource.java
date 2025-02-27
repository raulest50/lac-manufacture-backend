package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    // In ProveedorResource.java
    @GetMapping("/search_pag")
    public ResponseEntity<Page<Proveedor>> searchProveedores(
            @RequestParam("q") String searchText,
            @RequestParam("searchType") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Proveedor> proveedores = proveedorService.searchProveedores(searchText, searchType, page, size);
        return ResponseEntity.ok(proveedores);
    }


}
