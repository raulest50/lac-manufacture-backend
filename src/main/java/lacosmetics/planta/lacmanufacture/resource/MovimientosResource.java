package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.repo.MovimientoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientosResource {

    private final MovimientoRepo movimientoRepo;

}
