package exotic.app.planta.model.contabilidad;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa una cuenta contable en el Catálogo (Plan) de Cuentas.
 */
@Entity
@Table(name = "cuenta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CuentaContable {

    @Id
    @Column(length = 10)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCuenta tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "saldo_normal", nullable = false)
    private SaldoNormal saldoNormal;

    @Column(name = "cuenta_control", nullable = false)
    private boolean cuentaControl;

    public enum TipoCuenta { ACTIVO, PASIVO, PATRIMONIO, INGRESO, GASTO }

    public enum SaldoNormal { DEBITO, CREDITO }
}


    /*

    Código | Nombre | Tipo de CuentaContable | Saldo Normal | Descripción breve
1000 | Caja | ActivoFijo | Débito | Efectivo disponible en caja
1010 | Banco | ActivoFijo | Débito | Saldos en cuentas bancarias
1200 | Inventario Materias Primas | ActivoFijo | Débito | Materias primas en stock
1210 | Inventario WIP (Trabajo en Proceso) | ActivoFijo | Débito | Costos acumulados de producción aún en curso
1220 | Inventario Productos Terminados (FG) | ActivoFijo | Débito | Productos listos para la venta
1300 | ActivoFijo Fijo (Equipo y Mobiliario) | ActivoFijo | Débito | Valor histórico de activos fijos
1310 | Depreciación Acumulada | ActivoFijo (Contra) | Crédito | Depreciación total aplicada a activos fijos
2000 | Cuentas por Pagar – Proveedores | Pasivo | Crédito | Obligaciones pendientes de pago a proveedores
3000 | Capital Social | Patrimonio | Crédito | Aportes de socios o capital inicial
4000 | Ingresos por Ventas | Ingreso | Crédito | Ventas de productos y servicios
5000 | Costo de Ventas | Gasto | Débito | Costo de bienes vendidos
5100 | Gasto de Depreciación | Gasto | Débito | Cargo periódico por depreciación
5200 | Gasto por Scrap (Desperdicio) | Gasto | Débito | Pérdidas por material o producto defectuoso


     */





