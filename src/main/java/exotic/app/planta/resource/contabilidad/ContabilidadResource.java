package exotic.app.planta.resource.contabilidad;

import exotic.app.planta.model.activos.fijos.gestion.IncorporacionActivoHeader;
import exotic.app.planta.model.contabilidad.AsientoContable;
import exotic.app.planta.model.contabilidad.dto.search.DTO_SearchIncorporacionActivo;
import exotic.app.planta.model.contabilidad.dto.search.DTO_SearchTransaccionAlmacen;
import exotic.app.planta.model.inventarios.TransaccionAlmacen;
import exotic.app.planta.service.contabilidad.ContabilidadService;
import exotic.app.planta.repo.inventarios.TransaccionAlmacenHeaderRepo;
import exotic.app.planta.repo.compras.OrdenCompraRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controlador REST para operaciones contables.
 * Proporciona endpoints para la contabilización manual de transacciones.
 */
@RestController
@RequestMapping("/api/contabilidad")
@RequiredArgsConstructor
@Slf4j
public class ContabilidadResource {

    private final ContabilidadService contabilidadService;
    private final TransaccionAlmacenHeaderRepo transaccionAlmacenHeaderRepo;
    private final OrdenCompraRepo ordenCompraRepo;

    /**
     * Endpoint para obtener transacciones de almacén filtradas por estado contable y rango de fechas.
     * 
     * Este endpoint permite filtrar transacciones de almacén según diferentes criterios:
     * - Por estado contable (PENDIENTE, CONTABILIZADA, NO_APLICA)
     * - Por rango de fechas (fechaInicio y fechaFin)
     * 
     * El endpoint siempre filtra por transacciones de tipo OCM (orden de compra de materiales).
     * 
     * @param searchParams DTO con los parámetros de búsqueda
     * @return Lista paginada de transacciones que cumplen con los filtros
     * 
     * Ejemplos de uso:
     * 1. Buscar todas las transacciones pendientes:
     *    POST /api/contabilidad/transacciones
     *    { "estadoContable": "PENDIENTE" }
     * 
     * 2. Buscar transacciones contabilizadas en un rango de fechas:
     *    POST /api/contabilidad/transacciones
     *    { 
     *      "estadoContable": "CONTABILIZADA", 
     *      "fechaInicio": "2023-01-01T00:00:00", 
     *      "fechaFin": "2023-01-31T23:59:59" 
     *    }
     */
    @PostMapping("/transacciones")
    public ResponseEntity<Page<TransaccionAlmacen>> getTransacciones(
            @RequestBody DTO_SearchTransaccionAlmacen searchParams) {

        log.info("REST request para obtener transacciones de almacén. Estado: {}, Fecha inicio: {}, Fecha fin: {}", 
                 searchParams.getEstadoContable(), searchParams.getFechaInicio(), searchParams.getFechaFin());

        // Configurar paginación con ordenamiento por fecha descendente
        Pageable pageable = PageRequest.of(
            searchParams.getPage(), 
            searchParams.getSize(), 
            Sort.by("fechaTransaccion").descending()
        );

        // Tipo de entidad causante fijo en OCM como se requiere
        TransaccionAlmacen.TipoEntidadCausante tipoEntidadCausante = TransaccionAlmacen.TipoEntidadCausante.OCM;

        // Usar la consulta dinámica con todos los filtros
        Page<TransaccionAlmacen> transacciones = transaccionAlmacenHeaderRepo.findByFilters(
            searchParams.getEstadoContable(), 
            tipoEntidadCausante, 
            searchParams.getFechaInicio(), 
            searchParams.getFechaFin(), 
            pageable
        );

        return ResponseEntity.ok(transacciones);
    }

    /**
     * Endpoint para contabilizar manualmente una transacción de almacén.
     * Este endpoint se utiliza principalmente para transacciones de tipo OCM (ingreso de materiales)
     * que no se contabilizan automáticamente.
     * 
     * @param transaccionId ID de la transacción a contabilizar
     * @return Respuesta con el asiento contable creado o un mensaje de error
     */
    @PostMapping("/contabilizar-transaccion/{transaccionId}")
    public ResponseEntity<?> contabilizarTransaccion(@PathVariable int transaccionId) {
        try {
            // Buscar la transacción
            TransaccionAlmacen transaccion = transaccionAlmacenHeaderRepo.findById(transaccionId)
                    .orElseThrow(() -> new RuntimeException("Transacción no encontrada con ID: " + transaccionId));

            // Verificar que la transacción esté pendiente de contabilizar
            if (transaccion.getEstadoContable() == TransaccionAlmacen.EstadoContable.CONTABILIZADA) {
                return ResponseEntity.badRequest().body("La transacción ya ha sido contabilizada");
            }

            // Verificar que sea una transacción de tipo OCM
            if (transaccion.getTipoEntidadCausante() != TransaccionAlmacen.TipoEntidadCausante.OCM) {
                return ResponseEntity.badRequest().body("Solo se pueden contabilizar manualmente transacciones de tipo OCM");
            }

            // Buscar la orden de compra asociada
            var ordenCompra = ordenCompraRepo.findById(transaccion.getIdEntidadCausante())
                    .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada con ID: " + transaccion.getIdEntidadCausante()));

            // Calcular el monto total
            BigDecimal montoTotal = BigDecimal.ZERO;
            for (var itemOrdenCompra : ordenCompra.getItemsOrdenCompra()) {
                BigDecimal valorItem = BigDecimal.valueOf(itemOrdenCompra.getPrecioUnitario() * itemOrdenCompra.getCantidad());
                montoTotal = montoTotal.add(valorItem);
            }

            // Crear el asiento contable
            AsientoContable asiento = contabilidadService.registrarAsientoIngresoOCM(transaccion, ordenCompra, montoTotal);

            // Actualizar la transacción
            transaccion.setAsientoContable(asiento);
            transaccion.setEstadoContable(TransaccionAlmacen.EstadoContable.CONTABILIZADA);
            transaccionAlmacenHeaderRepo.save(transaccion);

            log.info("Transacción {} contabilizada manualmente. Asiento contable ID: {}", transaccionId, asiento.getId());

            return ResponseEntity.ok(asiento);
        } catch (Exception e) {
            log.error("Error al contabilizar transacción {}: {}", transaccionId, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error al contabilizar transacción: " + e.getMessage());
        }
    }

    /**
     * Endpoint para obtener incorporaciones de activos fijos filtradas por estado contable y rango de fechas.
     * 
     * Este endpoint permite filtrar incorporaciones según diferentes criterios:
     * - Por estado contable (PENDIENTE, CONTABILIZADA, NO_APLICA)
     * - Por estado de incorporación (En proceso, Completada, Cancelada)
     * - Por rango de fechas (fechaInicio y fechaFin)
     * 
     * @param searchParams DTO con los parámetros de búsqueda
     * @return Lista paginada de incorporaciones que cumplen con los filtros
     * 
     * Ejemplos de uso:
     * 1. Buscar todas las incorporaciones pendientes de contabilizar:
     *    POST /api/contabilidad/incorporaciones-activos
     *    { "estadoContable": "PENDIENTE" }
     * 
     * 2. Buscar incorporaciones completadas y contabilizadas en un rango de fechas:
     *    POST /api/contabilidad/incorporaciones-activos
     *    { 
     *      "estadoContable": "CONTABILIZADA", 
     *      "estado": 1,
     *      "fechaInicio": "2023-01-01T00:00:00", 
     *      "fechaFin": "2023-01-31T23:59:59" 
     *    }
     */
    @PostMapping("/incorporaciones-activos")
    public ResponseEntity<Page<IncorporacionActivoHeader>> getIncorporacionesActivos(
            @RequestBody DTO_SearchIncorporacionActivo searchParams) {

        log.info("REST request para obtener incorporaciones de activos. Estado contable: {}, Estado: {}, Fecha inicio: {}, Fecha fin: {}", 
                 searchParams.getEstadoContable(), searchParams.getEstado(), 
                 searchParams.getFechaInicio(), searchParams.getFechaFin());

        // Configurar paginación con ordenamiento por fecha descendente
        Pageable pageable = PageRequest.of(
            searchParams.getPage(), 
            searchParams.getSize(), 
            Sort.by("fechaIncorporacion").descending()
        );

        // Usar el servicio para realizar la búsqueda
        Page<IncorporacionActivoHeader> incorporaciones = contabilidadService.searchIncorporaciones(
            searchParams, 
            pageable
        );

        return ResponseEntity.ok(incorporaciones);
    }
}
