package exotic.app.planta.model.activos.fijos.compras;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import exotic.app.planta.model.compras.Proveedor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Representa una factura de compra para activos fijos.
 */
@Entity
@Table(name = "factura_compra_activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaCompraActivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_compra_activo_id", unique = true, updatable = false, nullable = false)
    private int facturaCompraActivoId;

    /**
     * Reference to the supplier (Proveedor) using the surrogate key.
     * This relationship uses the internal pk field rather than the business identifier
     * to maintain referential integrity even if the business ID changes.
     */
    @ManyToOne
    @JoinColumn(name = "proveedor_pk", referencedColumnName = "pk")
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "orden_compra_activo_id")
    private OrdenCompraActivo ordenCompraActivo;

    /**
     * Número de factura del proveedor
     */
    private String numeroFacturaProveedor;

    /**
     * Ruta al archivo de la factura
     */
    private String urlFactura;

    @CreationTimestamp
    private LocalDateTime fechaCompra;

    private double subTotal;
    private double iva;
    private double totalPagar;

    /**
     * 0: credito
     * 1: contado
     */
    private String condicionPago;

    // en días
    private int plazoPago;

    /**
     * 0: pendiente pago
     * 1: pagada
     */
    private int estadoPago;
}
