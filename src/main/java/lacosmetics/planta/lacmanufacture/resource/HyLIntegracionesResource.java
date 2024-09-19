package lacosmetics.planta.lacmanufacture.resource;


import lacosmetics.planta.lacmanufacture.model.GrupoMovimeintoMP;
import lacosmetics.planta.lacmanufacture.model.notPersisted.ReporteCompraDTA;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.service.HyLIntegracionService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/integracion")
@RequiredArgsConstructor
public class HyLIntegracionesResource {

    private final HyLIntegracionService HyLservice;

    @PostMapping("/codificar")
    public ResponseEntity<MateriaPrima> saveMovimiento(@RequestBody MateriaPrima materiaPrima){
        return ResponseEntity.created(URI.create("/codificar/materiaPrimaID")).body(HyLservice.codificarMateriaPrima(materiaPrima));
    }

    @PostMapping("/reportar_compra")
    public ResponseEntity<GrupoMovimeintoMP> Modify(@RequestBody ReporteCompraDTA reporteCompra){
        return ResponseEntity.created(URI.create("/reportar_compra/compraID")).body(HyLservice.reportarCompraHyL(reporteCompra));
    }


}
