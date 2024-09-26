package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
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


    // obtener lista materias primas, recibidas desde intergacion HyL, pendientes por completar atributos
    @GetMapping("get_mp_pendientes")
    public ResponseEntity<Page<MateriaPrima>> getPendientesMP(@RequestParam(value="page", defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok().body(productoService.getPendientesFromHyL(page, size));
    }

    @PostMapping("/save_mp")
    public ResponseEntity<MateriaPrima> saveMateriaPrima(@RequestBody MateriaPrima materiaPrima){
        return ResponseEntity.created(URI.create("/productos/productoID")).body(productoService.updateMateriaPrima(materiaPrima));
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

}
