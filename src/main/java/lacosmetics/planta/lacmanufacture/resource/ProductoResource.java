package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.dto.InsumoWithStockDTO;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/search_mprima")
    public ResponseEntity<Page<MateriaPrima>> search_mprima(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String search,
            @RequestParam String tipoBusqueda)
    {
        if(tipoBusqueda.equals("ID")){
            Optional<MateriaPrima> materiaPrimaOptional = productoService.findMateriaPrimaByProductoId(Integer.parseInt(search));
            if (materiaPrimaOptional.isPresent()) {
                List<MateriaPrima> materiaPrimaList = List.of(materiaPrimaOptional.get());
                Pageable pageable = PageRequest.of(page, size);
                return ResponseEntity.ok().body(new PageImpl<>(materiaPrimaList, pageable, 1));
            } else {
                return ResponseEntity.ok().body(new PageImpl<>(List.of(), PageRequest.of(page, size), 0));
            }
        } else{
            return ResponseEntity.ok().body(productoService.searchByName_MP(search, page, size));
        }
    }

    @GetMapping("/search_semi")
    public ResponseEntity<Page<SemiTerminado>> search_semi(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String search,
            @RequestParam String tipoBusqueda)
    {
        if(tipoBusqueda.equals("ID")){
            Optional<SemiTerminado> semiTerminadoOptional = productoService.findSemiTerminadoByProductoId(Integer.parseInt(search));
            if (semiTerminadoOptional.isPresent()) {
                List<SemiTerminado> semiTerminadoList = List.of(semiTerminadoOptional.get());
                Pageable pageable = PageRequest.of(page, size);
                return ResponseEntity.ok().body(new PageImpl<>(semiTerminadoList, pageable, 1));
            } else {
                return ResponseEntity.ok().body(new PageImpl<>(List.of(), PageRequest.of(page, size), 0));
            }
        } else{
            return ResponseEntity.ok().body(productoService.searchByName_S(search, page, size));
        }
    }

    @GetMapping("/search_terminado")
    public ResponseEntity<Page<Terminado>> search_terminado(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String search,
            @RequestParam String tipoBusqueda)
    {
        if(tipoBusqueda.equals("ID")){
            Optional<Terminado> terminadoOptional = productoService.findTerminadoByProductoId(Integer.parseInt(search));
            if (terminadoOptional.isPresent()) {
                List<Terminado> terminadoList = List.of(terminadoOptional.get());
                Pageable pageable = PageRequest.of(page, size);
                return ResponseEntity.ok().body(new PageImpl<>(terminadoList, pageable, 1));
            } else {
                return ResponseEntity.ok().body(new PageImpl<>(List.of(), PageRequest.of(page, size), 0));
            }
        } else{
            return ResponseEntity.ok().body(productoService.searchByName_T(search, page, size));
        }
    }


    @GetMapping("/search_semiytermi")
    public ResponseEntity<Page<ProductoStockDTO>> searchProductos(
            @RequestParam String searchTerm,
            @RequestParam String tipoBusqueda, // 'NOMBRE' or 'ID'
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<ProductoStockDTO> productos = productoService.searchTerminadoAndSemiTerminadoWithStock(searchTerm, tipoBusqueda, page, size);
        return ResponseEntity.ok().body(productos);
    }


    @GetMapping("/{productoId}/insumos_with_stock")
    public ResponseEntity<List<InsumoWithStockDTO>> getInsumosWithStock(@PathVariable int productoId) {
        List<InsumoWithStockDTO> insumosWithStock = productoService.getInsumosWithStock(productoId);
        return ResponseEntity.ok().body(insumosWithStock);
    }

}
