package lacosmetics.planta.lacmanufacture.resource.inventarios;

import lacosmetics.planta.lacmanufacture.model.compras.dto.recepcion.OCMReceptionInfoDTO;
import lacosmetics.planta.lacmanufacture.model.compras.dto.recepcion.SearchOCMFilterDTO;
import lacosmetics.planta.lacmanufacture.service.inventarios.IngresoAlmacenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingresos_almacen")
@RequiredArgsConstructor
public class IngresoAlmacenResource {

    private final IngresoAlmacenService ingresoAlmacenService;

    @PostMapping("/consulta_ocm_pendientes")
    /**
     * Devuelve las transacciones de almacén contables pendientes cuyo causante es una orden de compra
     * de materiales. Siempre responde de forma paginada para evitar respuestas excesivamente grandes.
     * <p>
     * El {@link SearchOCMFilterDTO} se utiliza para mantener compatibilidad de firma, pero actualmente
     * el servicio lista todas las pendientes sin aplicar filtros adicionales.
     */
    public ResponseEntity<Page<OCMReceptionInfoDTO>> consultaOcmPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody SearchOCMFilterDTO filter
    ) {
        Page<OCMReceptionInfoDTO> result = ingresoAlmacenService.consultaOCMPendientes(filter, page, size);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/consulta_ocm_recepciones")
    /**
     * Devuelve órdenes de compra (estado 2) junto con todas sus transacciones de almacén registradas.
     * El resultado siempre es paginado. Los filtros de proveedor y rango de fechas son opcionales:
     * si no se envían, no se aplican, devolviendo todas las órdenes disponibles según la paginación.
     */
    public ResponseEntity<Page<OCMReceptionInfoDTO>> consultaOcmRecepciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody SearchOCMFilterDTO filter
    ) {
        Page<OCMReceptionInfoDTO> result = ingresoAlmacenService.consultaOCMConRecepciones(filter, page, size);
        return ResponseEntity.ok(result);
    }

}
