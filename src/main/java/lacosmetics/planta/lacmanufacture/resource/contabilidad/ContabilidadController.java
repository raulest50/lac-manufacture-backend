package lacosmetics.planta.lacmanufacture.resource.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import lacosmetics.planta.lacmanufacture.model.contabilidad.CuentaContable;
import lacosmetics.planta.lacmanufacture.model.contabilidad.PeriodoContable;
import lacosmetics.planta.lacmanufacture.service.contabilidad.AsientoContableService;
import lacosmetics.planta.lacmanufacture.service.contabilidad.CuentaContableService;
import lacosmetics.planta.lacmanufacture.service.contabilidad.PeriodoContableService;
import lacosmetics.planta.lacmanufacture.service.contabilidad.ReporteContableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para el módulo de contabilidad.
 * Expone endpoints para gestionar cuentas contables, asientos contables,
 * períodos contables y generar reportes.
 */
@RestController
@RequestMapping("/api/contabilidad")
@RequiredArgsConstructor
@Slf4j
public class ContabilidadController {

    private final CuentaContableService cuentaContableService;
    private final AsientoContableService asientoContableService;
    private final PeriodoContableService periodoContableService;
    private final ReporteContableService reporteContableService;

    //
    // Endpoints para Catálogo de Cuentas
    //

    /**
     * Obtiene todas las cuentas contables.
     * 
     * @return Lista de cuentas contables
     */
    @GetMapping("/cuentas")
    public ResponseEntity<?> obtenerTodasLasCuentas() {
        log.info("REST request para obtener todas las cuentas contables");
        return ResponseEntity.ok(cuentaContableService.obtenerTodasLasCuentas());
    }

    /**
     * Obtiene una cuenta contable por su código.
     * 
     * @param codigo Código de la cuenta contable
     * @return La cuenta contable si existe, error 404 en caso contrario
     */
    @GetMapping("/cuentas/{codigo}")
    public ResponseEntity<?> obtenerCuentaPorCodigo(@PathVariable String codigo) {
        log.info("REST request para obtener cuenta contable con código: {}", codigo);

        Optional<CuentaContable> cuentaOpt = cuentaContableService.obtenerCuentaPorCodigo(codigo);

        if (cuentaOpt.isPresent()) {
            return ResponseEntity.ok(cuentaOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Cuenta no encontrada",
                            "mensaje", "No se encontró una cuenta con el código " + codigo
                    ));
        }
    }

    /**
     * Crea una nueva cuenta contable.
     * 
     * @param cuenta La cuenta contable a crear
     * @return La cuenta contable creada
     */
    @PostMapping("/cuentas")
    public ResponseEntity<?> crearCuenta(@RequestBody CuentaContable cuenta) {
        log.info("REST request para crear cuenta contable: {}", cuenta.getCodigo());

        try {
            CuentaContable nuevaCuenta = cuentaContableService.crearCuenta(cuenta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
        } catch (RuntimeException e) {
            log.error("Error al crear cuenta contable", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Datos inválidos",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    /**
     * Actualiza una cuenta contable existente.
     * 
     * @param codigo Código de la cuenta contable
     * @param cuenta Datos actualizados de la cuenta
     * @return La cuenta contable actualizada
     */
    @PutMapping("/cuentas/{codigo}")
    public ResponseEntity<?> actualizarCuenta(
            @PathVariable String codigo,
            @RequestBody CuentaContable cuenta) {
        log.info("REST request para actualizar cuenta contable con código: {}", codigo);

        try {
            CuentaContable cuentaActualizada = cuentaContableService.actualizarCuenta(codigo, cuenta);
            return ResponseEntity.ok(cuentaActualizada);
        } catch (RuntimeException e) {
            log.error("Error al actualizar cuenta contable", e);

            if (e.getMessage().contains("No existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Cuenta no encontrada",
                                "mensaje", e.getMessage()
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "Datos inválidos",
                                "mensaje", e.getMessage()
                        ));
            }
        }
    }

    //
    // Endpoints para Asientos Contables
    //

    /**
     * Obtiene todos los asientos contables, opcionalmente filtrados por período y estado.
     * 
     * @param periodoId ID del período contable (opcional)
     * @param estado Estado del asiento (opcional)
     * @return Lista de asientos contables
     */
    @GetMapping("/asientos")
    public ResponseEntity<?> obtenerTodosLosAsientos(
            @RequestParam(required = false) Long periodoId,
            @RequestParam(required = false) AsientoContable.EstadoAsiento estado) {
        log.info("REST request para obtener asientos contables. Período: {}, Estado: {}", periodoId, estado);

        return ResponseEntity.ok(asientoContableService.obtenerTodosLosAsientos(periodoId, estado));
    }

    /**
     * Obtiene un asiento contable por su ID.
     * 
     * @param id ID del asiento contable
     * @return El asiento contable si existe, error 404 en caso contrario
     */
    @GetMapping("/asientos/{id}")
    public ResponseEntity<?> obtenerAsientoPorId(@PathVariable Long id) {
        log.info("REST request para obtener asiento contable con ID: {}", id);

        Optional<AsientoContable> asientoOpt = asientoContableService.obtenerAsientoPorId(id);

        if (asientoOpt.isPresent()) {
            return ResponseEntity.ok(asientoOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Asiento no encontrado",
                            "mensaje", "No se encontró un asiento con el ID " + id
                    ));
        }
    }

    /**
     * Crea un nuevo asiento contable.
     * 
     * @param asiento El asiento contable a crear
     * @return El asiento contable creado
     */
    @PostMapping("/asientos")
    public ResponseEntity<?> crearAsiento(@RequestBody AsientoContable asiento) {
        log.info("REST request para crear asiento contable");

        try {
            AsientoContable nuevoAsiento = asientoContableService.crearAsiento(asiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAsiento);
        } catch (RuntimeException e) {
            log.error("Error al crear asiento contable", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Datos inválidos",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    /**
     * Actualiza un asiento contable existente.
     * 
     * @param id ID del asiento contable
     * @param asiento Datos actualizados del asiento
     * @return El asiento contable actualizado
     */
    @PutMapping("/asientos/{id}")
    public ResponseEntity<?> actualizarAsiento(
            @PathVariable Long id,
            @RequestBody AsientoContable asiento) {
        log.info("REST request para actualizar asiento contable con ID: {}", id);

        try {
            AsientoContable asientoActualizado = asientoContableService.actualizarAsiento(id, asiento);
            return ResponseEntity.ok(asientoActualizado);
        } catch (RuntimeException e) {
            log.error("Error al actualizar asiento contable", e);

            if (e.getMessage().contains("No existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Asiento no encontrado",
                                "mensaje", e.getMessage()
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "Operación no permitida",
                                "mensaje", e.getMessage()
                        ));
            }
        }
    }

    /**
     * Cambia el estado de un asiento contable.
     * 
     * @param id ID del asiento contable
     * @param estadoMap Mapa con el nuevo estado
     * @return Información sobre el cambio de estado
     */
    @PutMapping("/asientos/{id}/estado")
    public ResponseEntity<?> cambiarEstadoAsiento(
            @PathVariable Long id,
            @RequestBody Map<String, String> estadoMap) {
        log.info("REST request para cambiar estado de asiento contable con ID: {} a {}", id, estadoMap.get("estado"));

        try {
            AsientoContable.EstadoAsiento nuevoEstado = AsientoContable.EstadoAsiento.valueOf(estadoMap.get("estado"));
            AsientoContable asientoActualizado = asientoContableService.cambiarEstadoAsiento(id, nuevoEstado);

            return ResponseEntity.ok(Map.of(
                    "id", asientoActualizado.getId(),
                    "estado", asientoActualizado.getEstado(),
                    "mensaje", "El estado del asiento ha sido actualizado correctamente"
            ));
        } catch (IllegalArgumentException e) {
            log.error("Error al cambiar estado de asiento contable: estado inválido", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Estado inválido",
                            "mensaje", "El estado proporcionado no es válido"
                    ));
        } catch (RuntimeException e) {
            log.error("Error al cambiar estado de asiento contable", e);

            if (e.getMessage().contains("No existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Asiento no encontrado",
                                "mensaje", e.getMessage()
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "Operación no permitida",
                                "mensaje", e.getMessage()
                        ));
            }
        }
    }

    //
    // Endpoints para Períodos Contables
    //

    /**
     * Obtiene todos los períodos contables, opcionalmente filtrados por estado.
     * 
     * @param estado Estado del período (opcional)
     * @return Lista de períodos contables
     */
    @GetMapping("/periodos")
    public ResponseEntity<?> obtenerTodosLosPeriodos(
            @RequestParam(required = false) PeriodoContable.EstadoPeriodo estado) {
        log.info("REST request para obtener períodos contables. Estado: {}", estado);

        return ResponseEntity.ok(periodoContableService.obtenerTodosLosPeriodos(estado));
    }

    /**
     * Obtiene un período contable por su ID.
     * 
     * @param id ID del período contable
     * @return El período contable si existe, error 404 en caso contrario
     */
    @GetMapping("/periodos/{id}")
    public ResponseEntity<?> obtenerPeriodoPorId(@PathVariable Long id) {
        log.info("REST request para obtener período contable con ID: {}", id);

        Optional<PeriodoContable> periodoOpt = periodoContableService.obtenerPeriodoPorId(id);

        if (periodoOpt.isPresent()) {
            return ResponseEntity.ok(periodoOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Período no encontrado",
                            "mensaje", "No se encontró un período con el ID " + id
                    ));
        }
    }

    /**
     * Crea un nuevo período contable.
     * 
     * @param periodo El período contable a crear
     * @return El período contable creado
     */
    @PostMapping("/periodos")
    public ResponseEntity<?> crearPeriodo(@RequestBody PeriodoContable periodo) {
        log.info("REST request para crear período contable: {}", periodo.getNombre());

        try {
            PeriodoContable nuevoPeriodo = periodoContableService.crearPeriodo(periodo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPeriodo);
        } catch (RuntimeException e) {
            log.error("Error al crear período contable", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Datos inválidos",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    /**
     * Actualiza un período contable existente.
     * 
     * @param id ID del período contable
     * @param periodo Datos actualizados del período
     * @return El período contable actualizado
     */
    @PutMapping("/periodos/{id}")
    public ResponseEntity<?> actualizarPeriodo(
            @PathVariable Long id,
            @RequestBody PeriodoContable periodo) {
        log.info("REST request para actualizar período contable con ID: {}", id);

        try {
            PeriodoContable periodoActualizado = periodoContableService.actualizarPeriodo(id, periodo);
            return ResponseEntity.ok(periodoActualizado);
        } catch (RuntimeException e) {
            log.error("Error al actualizar período contable", e);

            if (e.getMessage().contains("No existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Período no encontrado",
                                "mensaje", e.getMessage()
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "Datos inválidos",
                                "mensaje", e.getMessage()
                        ));
            }
        }
    }

    /**
     * Cambia el estado de un período contable.
     * 
     * @param id ID del período contable
     * @param estadoMap Mapa con el nuevo estado
     * @return Información sobre el cambio de estado
     */
    @PutMapping("/periodos/{id}/estado")
    public ResponseEntity<?> cambiarEstadoPeriodo(
            @PathVariable Long id,
            @RequestBody Map<String, String> estadoMap) {
        log.info("REST request para cambiar estado de período contable con ID: {} a {}", id, estadoMap.get("estado"));

        try {
            PeriodoContable.EstadoPeriodo nuevoEstado = PeriodoContable.EstadoPeriodo.valueOf(estadoMap.get("estado"));
            PeriodoContable periodoActualizado = periodoContableService.cambiarEstadoPeriodo(id, nuevoEstado);

            return ResponseEntity.ok(Map.of(
                    "id", periodoActualizado.getId(),
                    "estado", periodoActualizado.getEstado(),
                    "mensaje", "El estado del período ha sido actualizado correctamente"
            ));
        } catch (IllegalArgumentException e) {
            log.error("Error al cambiar estado de período contable: estado inválido", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Estado inválido",
                            "mensaje", "El estado proporcionado no es válido"
                    ));
        } catch (RuntimeException e) {
            log.error("Error al cambiar estado de período contable", e);

            if (e.getMessage().contains("No existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Período no encontrado",
                                "mensaje", e.getMessage()
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "Operación no permitida",
                                "mensaje", e.getMessage()
                        ));
            }
        }
    }

    //
    // Endpoints para Reportes
    //

    /**
     * Genera el libro mayor para una cuenta contable en un período específico.
     * 
     * @param cuentaCodigo Código de la cuenta contable
     * @param periodoId ID del período contable
     * @return Lista de movimientos del libro mayor
     */
    @GetMapping("/libro-mayor")
    public ResponseEntity<?> generarLibroMayor(
            @RequestParam String cuentaCodigo,
            @RequestParam Long periodoId) {
        log.info("REST request para generar libro mayor. Cuenta: {}, Período: {}", cuentaCodigo, periodoId);

        try {
            List<ReporteContableService.MovimientoLibroMayor> movimientos = 
                    reporteContableService.generarLibroMayor(cuentaCodigo, periodoId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            log.error("Error al generar libro mayor", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Parámetros inválidos",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    /**
     * Genera el balance de comprobación para un período específico.
     * 
     * @param periodoId ID del período contable
     * @return Lista de saldos de cuentas para el balance de comprobación
     */
    @GetMapping("/balance-comprobacion")
    public ResponseEntity<?> generarBalanceComprobacion(@RequestParam Long periodoId) {
        log.info("REST request para generar balance de comprobación. Período: {}", periodoId);

        try {
            List<ReporteContableService.SaldoCuenta> saldos = 
                    reporteContableService.generarBalanceComprobacion(periodoId);
            return ResponseEntity.ok(saldos);
        } catch (RuntimeException e) {
            log.error("Error al generar balance de comprobación", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Parámetros inválidos",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    /**
     * Genera el balance general para un período específico.
     * 
     * @param periodoId ID del período contable
     * @return Mapa con los grupos de cuentas y totales del balance general
     */
    @GetMapping("/balance-general")
    public ResponseEntity<?> generarBalanceGeneral(@RequestParam Long periodoId) {
        log.info("REST request para generar balance general. Período: {}", periodoId);

        try {
            Map<String, Object> balanceGeneral = reporteContableService.generarBalanceGeneral(periodoId);
            return ResponseEntity.ok(balanceGeneral);
        } catch (RuntimeException e) {
            log.error("Error al generar balance general", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Parámetros inválidos",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    /**
     * Genera el estado de resultados para un período específico.
     * 
     * @param periodoId ID del período contable
     * @return Mapa con los grupos de cuentas y totales del estado de resultados
     */
    @GetMapping("/estado-resultados")
    public ResponseEntity<?> generarEstadoResultados(@RequestParam Long periodoId) {
        log.info("REST request para generar estado de resultados. Período: {}", periodoId);

        try {
            Map<String, Object> estadoResultados = reporteContableService.generarEstadoResultados(periodoId);
            return ResponseEntity.ok(estadoResultados);
        } catch (RuntimeException e) {
            log.error("Error al generar estado de resultados", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Parámetros inválidos",
                            "mensaje", e.getMessage()
                    ));
        }
    }
}
