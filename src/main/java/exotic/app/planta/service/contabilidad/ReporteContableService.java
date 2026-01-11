package exotic.app.planta.service.contabilidad;

import exotic.app.planta.model.contabilidad.AsientoContable;
import exotic.app.planta.model.contabilidad.CuentaContable;
import exotic.app.planta.model.contabilidad.LineaAsientoContable;
import exotic.app.planta.model.contabilidad.PeriodoContable;
import exotic.app.planta.repo.contabilidad.AsientoContableRepo;
import exotic.app.planta.repo.contabilidad.CuentaContableRepo;
import exotic.app.planta.repo.contabilidad.PeriodoContableRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para generar reportes contables.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteContableService {

    private final AsientoContableRepo asientoContableRepo;
    private final CuentaContableRepo cuentaContableRepo;
    private final PeriodoContableRepo periodoContableRepo;

    /**
     * Clase para representar un movimiento en el libro mayor.
     */
    public static class MovimientoLibroMayor {
        private LocalDateTime fecha;
        private Long numeroAsiento;
        private String descripcion;
        private BigDecimal debito;
        private BigDecimal credito;
        private BigDecimal saldoAcumulado;

        public MovimientoLibroMayor(LocalDateTime fecha, Long numeroAsiento, String descripcion,
                                    BigDecimal debito, BigDecimal credito, BigDecimal saldoAcumulado) {
            this.fecha = fecha;
            this.numeroAsiento = numeroAsiento;
            this.descripcion = descripcion;
            this.debito = debito;
            this.credito = credito;
            this.saldoAcumulado = saldoAcumulado;
        }

        // Getters y setters
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

        public Long getNumeroAsiento() { return numeroAsiento; }
        public void setNumeroAsiento(Long numeroAsiento) { this.numeroAsiento = numeroAsiento; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public BigDecimal getDebito() { return debito; }
        public void setDebito(BigDecimal debito) { this.debito = debito; }

        public BigDecimal getCredito() { return credito; }
        public void setCredito(BigDecimal credito) { this.credito = credito; }

        public BigDecimal getSaldoAcumulado() { return saldoAcumulado; }
        public void setSaldoAcumulado(BigDecimal saldoAcumulado) { this.saldoAcumulado = saldoAcumulado; }
    }

    /**
     * Clase para representar el saldo de una cuenta en el balance de comprobación.
     */
    public static class SaldoCuenta {
        private CuentaContable cuenta;
        private BigDecimal saldoDebito;
        private BigDecimal saldoCredito;
        private BigDecimal saldoNeto;

        public SaldoCuenta(CuentaContable cuenta, BigDecimal saldoDebito, BigDecimal saldoCredito) {
            this.cuenta = cuenta;
            this.saldoDebito = saldoDebito;
            this.saldoCredito = saldoCredito;

            // Calcular saldo neto según el tipo de cuenta
            if (cuenta.getSaldoNormal() == CuentaContable.SaldoNormal.DEBITO) {
                this.saldoNeto = saldoDebito.subtract(saldoCredito);
            } else {
                this.saldoNeto = saldoCredito.subtract(saldoDebito);
            }
        }

        // Getters y setters
        public CuentaContable getCuenta() { return cuenta; }
        public void setCuenta(CuentaContable cuenta) { this.cuenta = cuenta; }

        public BigDecimal getSaldoDebito() { return saldoDebito; }
        public void setSaldoDebito(BigDecimal saldoDebito) { this.saldoDebito = saldoDebito; }

        public BigDecimal getSaldoCredito() { return saldoCredito; }
        public void setSaldoCredito(BigDecimal saldoCredito) { this.saldoCredito = saldoCredito; }

        public BigDecimal getSaldoNeto() { return saldoNeto; }
        public void setSaldoNeto(BigDecimal saldoNeto) { this.saldoNeto = saldoNeto; }
    }

    /**
     * Clase para representar un grupo de cuentas en los estados financieros.
     */
    public static class GrupoCuentas {
        private String nombre;
        private List<CuentaSaldo> cuentas;
        private BigDecimal total;

        public GrupoCuentas(String nombre) {
            this.nombre = nombre;
            this.cuentas = new ArrayList<>();
            this.total = BigDecimal.ZERO;
        }

        public void agregarCuenta(CuentaSaldo cuenta) {
            this.cuentas.add(cuenta);
            this.total = this.total.add(cuenta.getSaldo());
        }

        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public List<CuentaSaldo> getCuentas() { return cuentas; }
        public void setCuentas(List<CuentaSaldo> cuentas) { this.cuentas = cuentas; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }

    /**
     * Clase para representar una cuenta con su saldo en los estados financieros.
     */
    public static class CuentaSaldo {
        private String codigo;
        private String nombre;
        private BigDecimal saldo;

        public CuentaSaldo(String codigo, String nombre, BigDecimal saldo) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.saldo = saldo;
        }

        // Getters y setters
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public BigDecimal getSaldo() { return saldo; }
        public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    }

    /**
     * Genera el libro mayor para una cuenta contable en un período específico.
     * 
     * @param cuentaCodigo Código de la cuenta contable
     * @param periodoId ID del período contable
     * @return Lista de movimientos del libro mayor
     * @throws RuntimeException si hay errores de validación
     */
    public List<MovimientoLibroMayor> generarLibroMayor(String cuentaCodigo, Long periodoId) {
        log.info("Generando libro mayor para cuenta: {}, período: {}", cuentaCodigo, periodoId);

        if (cuentaCodigo == null || periodoId == null) {
            log.error("Error al generar libro mayor: parámetros inválidos");
            throw new RuntimeException("Debe especificar el código de cuenta y el ID del período");
        }

        // Verificar que la cuenta existe
        CuentaContable cuenta = cuentaContableRepo.findById(cuentaCodigo)
                .orElseThrow(() -> {
                    log.error("Error al generar libro mayor: la cuenta {} no existe", cuentaCodigo);
                    return new RuntimeException("La cuenta no existe");
                });

        // Verificar que el período existe
        PeriodoContable periodo = periodoContableRepo.findById(periodoId)
                .orElseThrow(() -> {
                    log.error("Error al generar libro mayor: el período {} no existe", periodoId);
                    return new RuntimeException("El período no existe");
                });

        // Obtener todos los asientos del período que afectan a la cuenta
        List<AsientoContable> asientos = periodo.getAsientos().stream()
                .filter(a -> a.getEstado() == AsientoContable.EstadoAsiento.PUBLICADO)
                .filter(a -> a.getLineas().stream()
                        .anyMatch(l -> l.getCuentaCodigo().equals(cuentaCodigo)))
                .sorted(Comparator.comparing(AsientoContable::getFecha))
                .collect(Collectors.toList());

        // Generar los movimientos del libro mayor
        List<MovimientoLibroMayor> movimientos = new ArrayList<>();
        BigDecimal saldoAcumulado = BigDecimal.ZERO;

        for (AsientoContable asiento : asientos) {
            for (LineaAsientoContable linea : asiento.getLineas()) {
                if (linea.getCuentaCodigo().equals(cuentaCodigo)) {
                    BigDecimal debito = linea.getDebito() != null ? linea.getDebito() : BigDecimal.ZERO;
                    BigDecimal credito = linea.getCredito() != null ? linea.getCredito() : BigDecimal.ZERO;

                    // Actualizar saldo acumulado según el tipo de cuenta
                    if (cuenta.getSaldoNormal() == CuentaContable.SaldoNormal.DEBITO) {
                        saldoAcumulado = saldoAcumulado.add(debito).subtract(credito);
                    } else {
                        saldoAcumulado = saldoAcumulado.add(credito).subtract(debito);
                    }

                    movimientos.add(new MovimientoLibroMayor(
                            asiento.getFecha(),
                            asiento.getId(),
                            linea.getDescripcion(),
                            debito,
                            credito,
                            saldoAcumulado
                    ));
                }
            }
        }

        return movimientos;
    }

    /**
     * Genera el balance de comprobación para un período específico.
     * 
     * @param periodoId ID del período contable
     * @return Lista de saldos de cuentas para el balance de comprobación
     * @throws RuntimeException si hay errores de validación
     */
    /*
    public List<SaldoCuenta> generarBalanceComprobacion(Long periodoId) {
        log.info("Generando balance de comprobación para período: {}", periodoId);

        if (periodoId == null) {
            log.error("Error al generar balance de comprobación: parámetros inválidos");
            throw new RuntimeException("Debe especificar el ID del período");
        }

        // Verificar que el período existe
        PeriodoContable periodo = periodoContableRepo.findById(periodoId)
                .orElseThrow(() -> {
                    log.error("Error al generar balance de comprobación: el período {} no existe", periodoId);
                    return new RuntimeException("El período no existe");
                });

        // Obtener todas las cuentas
        List<CuentaContable> cuentas = cuentaContableRepo.findAll();

        // Obtener todos los asientos publicados del período
        List<AsientoContable> asientos = periodo.getAsientos().stream()
                .filter(a -> a.getEstado() == AsientoContable.EstadoAsiento.PUBLICADO)
                .collect(Collectors.toList());

        // Calcular saldos para cada cuenta
        List<SaldoCuenta> saldos = new ArrayList<>();

        for (CuentaContable cuenta : cuentas) {
            BigDecimal totalDebitos = BigDecimal.ZERO;
            BigDecimal totalCreditos = BigDecimal.ZERO;

            for (AsientoContable asiento : asientos) {
                for (LineaAsientoContable linea : asiento.getLineas()) {
                    if (linea.getCuentaCodigo().equals(cuenta.getCodigo())) {
                        if (linea.getDebito() != null) {
                            totalDebitos = totalDebitos.add(linea.getDebito());
                        }
                        if (linea.getCredito() != null) {
                            totalCreditos = totalCreditos.add(linea.getCredito());
                        }
                    }
                }
            }

            // Solo incluir cuentas con movimientos
            if (totalDebitos.compareTo(BigDecimal.ZERO) != 0 || totalCreditos.compareTo(BigDecimal.ZERO) != 0) {
                saldos.add(new SaldoCuenta(cuenta, totalDebitos, totalCreditos));
            }
        }

        return saldos;
    }
    */


    /**
     * Genera el balance general para un período específico.
     * 
     * @param periodoId ID del período contable
     * @return Mapa con los grupos de cuentas y totales del balance general
     * @throws RuntimeException si hay errores de validación
     */
/*    public Map<String, Object> generarBalanceGeneral(Long periodoId) {
        log.info("Generando balance general para período: {}", periodoId);

        if (periodoId == null) {
            log.error("Error al generar balance general: parámetros inválidos");
            throw new RuntimeException("Debe especificar el ID del período");
        }

        // Obtener los saldos de todas las cuentas
        List<SaldoCuenta> saldosCuentas = generarBalanceComprobacion(periodoId);

        // Crear grupos para el balance general
        GrupoCuentas activos = new GrupoCuentas("ACTIVOS");
        GrupoCuentas pasivos = new GrupoCuentas("PASIVOS");
        GrupoCuentas patrimonio = new GrupoCuentas("PATRIMONIO");

        // Clasificar las cuentas en los grupos correspondientes
        for (SaldoCuenta saldo : saldosCuentas) {
            CuentaContable cuenta = saldo.getCuenta();
            BigDecimal valorAbsoluto = saldo.getSaldoNeto().abs();

            switch (cuenta.getTipo()) {
                case ACTIVO:
                    activos.agregarCuenta(new CuentaSaldo(
                            cuenta.getCodigo(),
                            cuenta.getNombre(),
                            valorAbsoluto
                    ));
                    break;
                case PASIVO:
                    pasivos.agregarCuenta(new CuentaSaldo(
                            cuenta.getCodigo(),
                            cuenta.getNombre(),
                            valorAbsoluto
                    ));
                    break;
                case PATRIMONIO:
                    patrimonio.agregarCuenta(new CuentaSaldo(
                            cuenta.getCodigo(),
                            cuenta.getNombre(),
                            valorAbsoluto
                    ));
                    break;
                default:
                    // Ignorar cuentas de ingresos y gastos para el balance general
                    break;
            }
        }

        // Crear el resultado
        List<GrupoCuentas> grupos = new ArrayList<>();
        grupos.add(activos);
        grupos.add(pasivos);
        grupos.add(patrimonio);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("grupos", grupos);
        resultado.put("total", activos.getTotal());

        return resultado;
    }*/


    /**
     * Genera el estado de resultados para un período específico.
     * 
     * @param periodoId ID del período contable
     * @return Mapa con los grupos de cuentas y totales del estado de resultados
     * @throws RuntimeException si hay errores de validación
     */
    /*public Map<String, Object> generarEstadoResultados(Long periodoId) {
        log.info("Generando estado de resultados para período: {}", periodoId);

        if (periodoId == null) {
            log.error("Error al generar estado de resultados: parámetros inválidos");
            throw new RuntimeException("Debe especificar el ID del período");
        }

        // Obtener los saldos de todas las cuentas
        List<SaldoCuenta> saldosCuentas = generarBalanceComprobacion(periodoId);

        // Crear grupos para el estado de resultados
        GrupoCuentas ingresos = new GrupoCuentas("INGRESOS");
        GrupoCuentas gastos = new GrupoCuentas("GASTOS");

        // Clasificar las cuentas en los grupos correspondientes
        for (SaldoCuenta saldo : saldosCuentas) {
            CuentaContable cuenta = saldo.getCuenta();
            BigDecimal valorAbsoluto = saldo.getSaldoNeto().abs();

            switch (cuenta.getTipo()) {
                case INGRESO:
                    ingresos.agregarCuenta(new CuentaSaldo(
                            cuenta.getCodigo(),
                            cuenta.getNombre(),
                            valorAbsoluto
                    ));
                    break;
                case GASTO:
                    gastos.agregarCuenta(new CuentaSaldo(
                            cuenta.getCodigo(),
                            cuenta.getNombre(),
                            valorAbsoluto
                    ));
                    break;
                default:
                    // Ignorar cuentas de activo, pasivo y patrimonio para el estado de resultados
                    break;
            }
        }

        // Calcular la utilidad o pérdida
        BigDecimal resultado = ingresos.getTotal().subtract(gastos.getTotal());

        // Crear el resultado
        List<GrupoCuentas> grupos = new ArrayList<>();
        grupos.add(ingresos);
        grupos.add(gastos);

        Map<String, Object> resultadoMap = new HashMap<>();
        resultadoMap.put("grupos", grupos);
        resultadoMap.put("total", resultado);

        return resultadoMap;
    }*/
}
