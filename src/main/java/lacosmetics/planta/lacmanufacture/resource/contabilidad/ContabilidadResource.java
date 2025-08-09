package lacosmetics.planta.lacmanufacture.resource.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.service.contabilidad.ContabilidadService;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}