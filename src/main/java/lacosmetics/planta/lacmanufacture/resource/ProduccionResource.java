package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO;
import lacosmetics.planta.lacmanufacture.service.ProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/produccion")
@RequiredArgsConstructor
public class ProduccionResource {

    private final ProduccionService produccionService;


    @PostMapping("/save")
    public ResponseEntity<OrdenProduccion> saveOrdenProduccion(@RequestBody OrdenProduccionDTO ordenProduccionDTO){
        return ResponseEntity.created(URI.create("/ordenes/ordenID")).body(produccionService.saveOrdenProduccion(ordenProduccionDTO));
    }


}
