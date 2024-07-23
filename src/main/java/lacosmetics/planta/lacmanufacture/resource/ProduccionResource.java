package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
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
    public ResponseEntity<Page<OrdenProduccion>> getWorkload(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam int zona_id
    )
    {
        return ResponseEntity.ok().body(produccionService.getWorkloadByZona(zona_id, page, size));
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

    @PostMapping
    public ResponseEntity<OrdenProduccion> saveOrdenProduccion(@RequestBody OrdenProduccion ordenProduccion){
        return ResponseEntity.created(URI.create("/ordenes/ordenID")).body(produccionService.saveOrdenProduccion(ordenProduccion));
    }





}
