package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.contabilidad.Cuenta;
import lacosmetics.planta.lacmanufacture.model.contabilidad.Cuenta.SaldoNormal;
import lacosmetics.planta.lacmanufacture.model.contabilidad.Cuenta.TipoCuenta;
import lacosmetics.planta.lacmanufacture.repo.contabilidad.CuentaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CuentasInitializer {

    private final CuentaRepo cuentaRepository;

    /**
     * Inicializa la tabla 'cuenta' con el catálogo mínimo
     * sólo si aún no existen registros.
     */
    public void initializeCuentas() {
        if (cuentaRepository.count() == 0) {
            List<Cuenta> cuentas = List.of(
                    new Cuenta("1000", "Caja", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new Cuenta("1010", "Banco", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new Cuenta("1200", "Inventario Materiales (M)", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new Cuenta("1210", "Inventario WIP (Trabajo en Proceso)", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new Cuenta("1220", "Inventario Productos Terminados (FG)", TipoCuenta.ACTIVO, SaldoNormal.DEBITO, false),
                    new Cuenta("2000", "Cuentas por Pagar – Proveedores", TipoCuenta.PASIVO, SaldoNormal.CREDITO, true),
                    new Cuenta("5200", "Gasto por Scrap (Desperdicio)", TipoCuenta.GASTO, SaldoNormal.DEBITO, false)
            );
            cuentaRepository.saveAll(cuentas);
            System.out.println(">> Tabla de cuentas inicializada con valores por defecto");
        }
    }
}
