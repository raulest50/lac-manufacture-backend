package lacosmetics.planta.lacmanufacture.service.contabilidad;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import lacosmetics.planta.lacmanufacture.model.contabilidad.CuentaContableCodigo;
import lacosmetics.planta.lacmanufacture.model.contabilidad.LineaAsientoContable;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.AsientoContableRepo;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.CuentaContableRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar operaciones contables.
 * Encapsula la lógica de creación y validación de asientos contables.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContabilidadService {

    private final AsientoContableRepo asientoContableRepo;
    private final CuentaContableRepo cuentaContableRepo;

    /**
     * Registra un asiento contable para un ingreso de mercancía por OCM
     * 
     * @param transaccion La transacción de almacén
     * @param ocm La orden de compra de materiales
     * @param montoTotal El monto total de la transacción
     * @return El asiento contable creado
     */
    public AsientoContable registrarAsientoIngresoOCM(
            TransaccionAlmacen transaccion, 
            OrdenCompraMateriales ocm,
            BigDecimal montoTotal) {

        // Validar que las cuentas existan
        validarCuentasExisten(
            CuentaContableCodigo.INVENTARIO_MATERIAS_PRIMAS.getCodigo(),
            CuentaContableCodigo.CUENTAS_POR_PAGAR_PROVEEDORES.getCodigo()
        );

        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(LocalDateTime.now());
        asiento.setDescripcion("Ingreso de mercancía por OCM #" + ocm.getOrdenCompraId());
        asiento.setModulo("INVENTARIO");
        asiento.setDocumentoOrigen("OCM-" + ocm.getOrdenCompraId());
        asiento.setEstado(AsientoContable.EstadoAsiento.PUBLICADO);

        List<LineaAsientoContable> lineas = new ArrayList<>();

        // 1. Débito a Inventario Materias Primas
        lineas.add(crearLineaAsiento(
            asiento,
            CuentaContableCodigo.INVENTARIO_MATERIAS_PRIMAS.getCodigo(),
            montoTotal,
            BigDecimal.ZERO,
            "Ingreso de materias primas a inventario"
        ));

        // 2. Crédito a Cuentas por Pagar - Proveedores
        lineas.add(crearLineaAsiento(
            asiento,
            CuentaContableCodigo.CUENTAS_POR_PAGAR_PROVEEDORES.getCodigo(),
            BigDecimal.ZERO,
            montoTotal,
            "Obligación con proveedor " + ocm.getProveedor().getNombre()
        ));

        asiento.setLineas(lineas);

        // Validar que el asiento esté balanceado
        validarCuadreContable(asiento);

        return asientoContableRepo.save(asiento);
    }

    /**
     * Crea una línea de asiento contable
     */
    private LineaAsientoContable crearLineaAsiento(
            AsientoContable asiento, 
            String cuentaCodigo, 
            BigDecimal debito, 
            BigDecimal credito, 
            String descripcion) {
        LineaAsientoContable linea = new LineaAsientoContable();
        linea.setAsientoContable(asiento);
        linea.setCuentaCodigo(cuentaCodigo);
        linea.setDebito(debito);
        linea.setCredito(credito);
        linea.setDescripcion(descripcion);
        return linea;
    }

    /**
     * Valida que las cuentas contables existan en el sistema
     */
    private void validarCuentasExisten(String... codigos) {
        for (String codigo : codigos) {
            if (!cuentaContableRepo.existsById(codigo)) {
                throw new RuntimeException("La cuenta contable con código " + codigo + " no existe");
            }
        }
    }

    /**
     * Registra un asiento contable para un ingreso de producto terminado (BACKFLUSH)
     * 
     * @param transaccion La transacción de almacén
     * @param ordenProduccion La orden de producción
     * @param montoTotal El monto total de la transacción
     * @return El asiento contable creado
     */
    public AsientoContable registrarAsientoBackflush(
            TransaccionAlmacen transaccion, 
            OrdenProduccion ordenProduccion,
            BigDecimal montoTotal) {

        // Validar que las cuentas existan
        validarCuentasExisten(
            CuentaContableCodigo.INVENTARIO_PRODUCTOS_TERMINADOS.getCodigo(),
            CuentaContableCodigo.INVENTARIO_WIP.getCodigo()
        );

        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(LocalDateTime.now());
        asiento.setDescripcion("Ingreso de producto terminado por OP #" + ordenProduccion.getOrdenId());
        asiento.setModulo("PRODUCCION");
        asiento.setDocumentoOrigen("OP-" + ordenProduccion.getOrdenId());
        asiento.setEstado(AsientoContable.EstadoAsiento.PUBLICADO);

        List<LineaAsientoContable> lineas = new ArrayList<>();

        // 1. Débito a Inventario Productos Terminados
        lineas.add(crearLineaAsiento(
            asiento,
            CuentaContableCodigo.INVENTARIO_PRODUCTOS_TERMINADOS.getCodigo(),
            montoTotal,
            BigDecimal.ZERO,
            "Ingreso de producto terminado a inventario"
        ));

        // 2. Crédito a Inventario WIP (Work in Progress)
        lineas.add(crearLineaAsiento(
            asiento,
            CuentaContableCodigo.INVENTARIO_WIP.getCodigo(),
            BigDecimal.ZERO,
            montoTotal,
            "Salida de WIP por finalización de OP #" + ordenProduccion.getOrdenId()
        ));

        asiento.setLineas(lineas);

        // Validar que el asiento esté balanceado
        validarCuadreContable(asiento);

        return asientoContableRepo.save(asiento);
    }

    /**
     * Valida que un asiento contable esté balanceado (débitos = créditos)
     */
    private void validarCuadreContable(AsientoContable asiento) {
        BigDecimal totalDebitos = asiento.getLineas().stream()
            .map(LineaAsientoContable::getDebito)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditos = asiento.getLineas().stream()
            .map(LineaAsientoContable::getCredito)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebitos.compareTo(totalCreditos) != 0) {
            throw new RuntimeException(
                "El asiento contable no está balanceado: " +
                "Débitos=" + totalDebitos + ", Créditos=" + totalCreditos
            );
        }
    }
}
