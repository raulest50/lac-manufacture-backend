package lacosmetics.planta.lacmanufacture.model.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen.EstadoContable;

import java.time.LocalDateTime;

/**
 * DTO para encapsular los parámetros de búsqueda de transacciones de almacén.
 * 
 * Este DTO se utiliza en el endpoint POST /api/contabilidad/transacciones para filtrar
 * transacciones de almacén según diferentes criterios:
 * 
 * - Por estado contable (PENDIENTE, CONTABILIZADA, NO_APLICA)
 * - Por rango de fechas (fechaInicio y fechaFin)
 * 
 * El endpoint siempre filtra por transacciones de tipo OCM (orden de compra de materiales).
 * 
 * Ejemplos de uso:
 * 1. Buscar todas las transacciones pendientes:
 *    { "estadoContable": "PENDIENTE" }
 * 
 * 2. Buscar transacciones contabilizadas en un rango de fechas:
 *    { 
 *      "estadoContable": "CONTABILIZADA", 
 *      "fechaInicio": "2023-01-01T00:00:00", 
 *      "fechaFin": "2023-01-31T23:59:59" 
 *    }
 * 
 * 3. Buscar todas las transacciones en un rango de fechas:
 *    { 
 *      "fechaInicio": "2023-01-01T00:00:00", 
 *      "fechaFin": "2023-01-31T23:59:59" 
 *    }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_SearchTransaccionAlmacen {
    
    private EstadoContable estadoContable;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaInicio;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaFin;
    
    // Parámetros de paginación
    private int page = 0;
    private int size = 10;
}