package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.contabilidad.CuentaContable;
import lacosmetics.planta.lacmanufacture.model.contabilidad.CuentaContable.SaldoNormal;
import lacosmetics.planta.lacmanufacture.model.contabilidad.CuentaContable.TipoCuenta;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.CuentaContableRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CuentasInitializer {

    private final CuentaContableRepo cuentaContableRepository;

    /**
     * Inicializa la tabla 'cuenta' con el catálogo mínimo
     * sólo si aún no existen registros.
     */
    public void initializeCuentas() {
        if (cuentaContableRepository.count() == 0) {
            List<CuentaContable> cuentaContables = List.of(
                    new CuentaContable("1000", "Caja", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new CuentaContable("1010", "Banco", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new CuentaContable("1200", "Inventario Materiales (M)", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new CuentaContable("1210", "Inventario WIP (Trabajo en Proceso)", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new CuentaContable("1220", "Inventario Productos Terminados (FG)", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new CuentaContable("1300", "Activos Fijos", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new CuentaContable("1310", "Depreciación Acumulada", TipoCuenta.ACTIVO, SaldoNormal.CREDITO, false),
                    new CuentaContable("2000", "Cuentas por Pagar – Proveedores", TipoCuenta.PASIVO, SaldoNormal.CREDITO, true),
                    new CuentaContable("5100", "Gasto por Depreciación", TipoCuenta.GASTO, SaldoNormal.DEBITO, false),
                    new CuentaContable("5200", "Gasto por Scrap (Desperdicio)", TipoCuenta.GASTO, SaldoNormal.DEBITO, false)
            );
            cuentaContableRepository.saveAll(cuentaContables);
            System.out.println(">> Tabla de cuentaContables inicializada con valores por defecto");
        }
    }
}
