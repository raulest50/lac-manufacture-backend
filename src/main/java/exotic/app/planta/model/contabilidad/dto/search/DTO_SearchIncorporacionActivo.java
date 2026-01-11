package exotic.app.planta.model.contabilidad.dto.search;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import exotic.app.planta.model.activos.fijos.gestion.IncorporacionActivoHeader.EstadoContable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para encapsular los parámetros de búsqueda de incorporaciones de activos fijos.
 *
 * Este DTO se utiliza para filtrar incorporaciones según diferentes criterios:
 * - Por estado contable (PENDIENTE, CONTABILIZADA, NO_APLICA)
 * - Por estado de incorporación (opcional)
 * - Por rango de fechas (fechaInicio y fechaFin)
 *
 * Ejemplos de uso:
 * 1. Buscar todas las incorporaciones pendientes de contabilizar:
 *    { "estadoContable": "PENDIENTE" }
 *
 * 2. Buscar incorporaciones completadas y contabilizadas en un rango de fechas:
 *    {
 *      "estadoContable": "CONTABILIZADA",
 *      "estado": 1,
 *      "fechaInicio": "2023-01-01T00:00:00",
 *      "fechaFin": "2023-01-31T23:59:59"
 *    }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_SearchIncorporacionActivo {

    private EstadoContable estadoContable;

    /**
     * Estado de la incorporación (opcional)
     * 0: En proceso
     * 1: Completada
     * 2: Cancelada
     */
    private Integer estado;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaFin;

    // Parámetros de paginación
    private int page = 0;
    private int size = 10;
}
