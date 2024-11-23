package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.model.Compra;
import lacosmetics.planta.lacmanufacture.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class ComprasResource {

    private final ComprasService compraService;

    @PostMapping("/save")
    public ResponseEntity<Compra> saveCompra(@RequestBody Compra compra) {
        Compra savedCompra = compraService.saveCompra(compra);
        return ResponseEntity.created(URI.create("/compras/" + savedCompra.getCompraId())).body(savedCompra);
    }

}
