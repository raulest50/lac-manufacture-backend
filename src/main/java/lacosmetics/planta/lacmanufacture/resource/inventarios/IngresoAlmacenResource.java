package lacosmetics.planta.lacmanufacture.resource.inventarios;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.compras.dto.recepcion.SearchOCMFilterDTO;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.service.inventarios.IngresoAlmacenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;

import java.util.Map;

@RestController
@RequestMapping("/ingresos_almacen")
@RequiredArgsConstructor
public class IngresoAlmacenResource {

    private final IngresoAlmacenService ingresoAlmacenService;
    private final TransaccionAlmacenHeaderRepo transaccionAlmacenHeaderRepo;

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


    /**
     * Este metodo es para listar todas las transacciones de almacen relacionadas con una OCM.
     * @return
     */
    @GetMapping("/consultar_transin_de_ocm")
    public ResponseEntity<?> ConsultarTransaccionesAlmacenDeOCM(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam int ordenCompraId
    ){
        try {
            List<TransaccionAlmacen> transacciones = ingresoAlmacenService.consultarTransaccionesAlmacenDeOCM(
                    ordenCompraId, page, size
            );
            return ResponseEntity.ok(transacciones);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * Para asociar un ingreso de materiales, o transaccion de almacen de ingreso, a una OCM.
     * @param ordenCompraMateriales
     * @return
     */
    @PostMapping("/registrar_tran_ingreso_ocm")
    public ResponseEntity<?> RegistrarTransaccionAlmacenIngresoOCM(
            @RequestBody OrdenCompraMateriales ordenCompraMateriales

    ){
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        return ResponseEntity.status(405).body(unsupportedOperationException.getMessage());
    }

}
