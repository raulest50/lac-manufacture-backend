package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.OrdenSeguimiento;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO;
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


    @GetMapping("/get_workload_by_responsable")
    public ResponseEntity<Page<OrdenProduccion>> getOrdenesProdByZona(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam int zonaId
    )
    {
        return ResponseEntity.ok().body(produccionService.getOrdenesProdByResponsable(zonaId, page, size));
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

    @PostMapping("/save")
    public ResponseEntity<OrdenProduccion> saveOrdenProduccion(@RequestBody OrdenProduccionDTO ordenProduccionDTO){
        return ResponseEntity.created(URI.create("/ordenes/ordenID")).body(produccionService.saveOrdenProduccion(ordenProduccionDTO));
    }

    @GetMapping("/update_oseg_estado")
    public ResponseEntity<OrdenSeguimiento> updateEstadoOrdenSeguimiento(
            @RequestParam int seguimientoId,
            @RequestParam int estado
    )
    {
        return ResponseEntity.created(URI.create("/ordenes/ordenID")).body(produccionService.updateEstadoOrdenSeguimiento(seguimientoId, estado));
    }



}
