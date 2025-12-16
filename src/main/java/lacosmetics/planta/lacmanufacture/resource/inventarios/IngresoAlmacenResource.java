package lacosmetics.planta.lacmanufacture.resource.inventarios;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.compras.dto.recepcion.SearchOCMFilterDTO;
import lacosmetics.planta.lacmanufacture.service.inventarios.IngresoAlmacenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingresos_almacen")
@RequiredArgsConstructor
public class IngresoAlmacenResource {

    private final IngresoAlmacenService ingresoAlmacenService;

    @GetMapping("ocms_pendientes_ingreso")
    public ResponseEntity<List<OrdenCompraMateriales>> ConsultarOCMsPendientesRecepcion(
            @RequestParam int page,
            @RequestParam int size,
            @ModelAttribute SearchOCMFilterDTO filterDTO
    ) {
        Page<OrdenCompraMateriales> pageResult = ingresoAlmacenService.consultarOCMsPendientesRecepcion(
                filterDTO,
                page,
                size
        );
        
        // Convert Page to List for frontend compatibility
        List<OrdenCompraMateriales> ordenes = pageResult.getContent();
        
        return ResponseEntity.ok(ordenes);
    }

}
