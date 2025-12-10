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
    public ResponseEntity<Page<OCMReceptionInfoDTO>> consultaOcmPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody SearchOCMFilterDTO filter
    ) {
        Page<OCMReceptionInfoDTO> result = ingresoAlmacenService.consultaOCMPendientes(filter, page, size);
        return ResponseEntity.ok(result);
    }

}
