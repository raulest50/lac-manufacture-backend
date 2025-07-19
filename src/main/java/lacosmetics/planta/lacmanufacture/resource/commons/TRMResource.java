package lacosmetics.planta.lacmanufacture.resource.commons;

import lacosmetics.planta.lacmanufacture.model.commons.TRM;
import lacosmetics.planta.lacmanufacture.service.commons.TRMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Recurso para consultar la Tasa Representativa del Mercado (TRM)
 * de la Superintendencia Financiera de Colombia
 */
@RestController
@RequestMapping("/api/trm")
@Slf4j
@RequiredArgsConstructor
public class TRMResource {

    private final TRMService trmService;

    /**
     * Endpoint para obtener la TRM actual
     * @return Respuesta con la TRM actual
     */
    @GetMapping
    public ResponseEntity<TRM> getTRMActual() {
        log.info("Recibida solicitud para obtener TRM actual");
        return ResponseEntity.ok(trmService.getTRMActual());
    }

    /**
     * Endpoint para obtener la TRM por fecha
     * @param fecha Fecha para la cual se requiere la TRM (formato: yyyy-MM-dd)
     * @return Respuesta con la TRM para la fecha especificada
     */
    @GetMapping("/por-fecha")
    public ResponseEntity<TRM> getTRMPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        log.info("Recibida solicitud para obtener TRM por fecha: {}", fecha);
        return ResponseEntity.ok(trmService.getTRMPorFecha(fecha));
    }
}