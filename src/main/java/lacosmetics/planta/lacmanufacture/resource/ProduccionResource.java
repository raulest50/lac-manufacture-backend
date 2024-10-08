package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.OrdenSeguimiento;
import lacosmetics.planta.lacmanufacture.service.ProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/produccion")
@RequiredArgsConstructor
public class ProduccionResource {

    private final ProduccionService produccionService;

    @GetMapping("/get_workload")
    public ResponseEntity<Page<OrdenSeguimiento>> getWorkload(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam int zonaId
    )
    {
        return ResponseEntity.ok().body(produccionService.getWorkloadByZona(zonaId, page, size));
    }


    @GetMapping("/get_orden_prod_by_zona")
    public ResponseEntity<Page<OrdenProduccion>> getOrdenesProdByZona(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam int zonaId
    )
    {
        return ResponseEntity.ok().body(produccionService.getOrdenesProdByZona(zonaId, page, size));
    }


    @GetMapping("/get_by_estado")
    public ResponseEntity<Page<OrdenProduccion>> getAllActive(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam int estado
    )
    {
        return ResponseEntity.ok().body(produccionService.getAllByEstado(page, size, estado));
    }

/*    @PostMapping("/save")
    public ResponseEntity<OrdenProduccion> saveOrdenProduccion(@RequestBody ProduccionService.OrdenProduccionDTA ordenProduccionDTA){
        return ResponseEntity.created(URI.create("/ordenes/ordenID")).body(produccionService.saveOrdenProduccion(ordenProduccionDTA));
    }*/

/*    @GetMapping("/update_oseg_estado")
    public ResponseEntity<OrdenSeguimiento> updateEstadoOrdenSeguimiento(
            @RequestParam int seguimientoId,
            @RequestParam int estado
    )
    {
        return ResponseEntity.created(URI.create("/ordenes/ordenID")).body(produccionService.updateEstadoOrdenSeguimiento(seguimientoId, estado));
    }*/



}
