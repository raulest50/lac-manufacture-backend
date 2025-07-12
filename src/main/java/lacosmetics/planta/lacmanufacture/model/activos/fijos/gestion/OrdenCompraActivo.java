package lacosmetics.planta.lacmanufacture.model.activos.fijos.gestion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Representa una orden de compra para activos fijos.
 */
@Entity
@Table(name = "orden_compra_activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraActivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_compra_activo_id", unique = true, updatable = false, nullable = false)
    private int ordenCompraActivoId;

    @CreationTimestamp
    private LocalDateTime fechaEmision;

    private LocalDateTime fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", referencedColumnName = "id")
    private Proveedor proveedor;

    private double subTotal;
    private double iva;
    private double totalPagar;

    /**
     * 0: credito
     * 1: contado
     */
    private String condicionPago;

    private String tiempoEntrega;

    private int plazoPago;

    /**
     * la cotizacion principal, la que se selecciono para hacer la compra
     */
    private String cotizacionUrl;

    /**
     * lista de cotizaciones con el proposito de reforzar que para
     * hacer una compra de activos se deberian hacer varias cotizaciones
     * siempre que sea posible para encontrar el mejor proveedor.
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "cotizaciones_urls")
    private String[] cotizacionesUrls;

    /**
     * -1: cancelada
     *  0: pendiente liberacion
     *  1: pendiente envio
     *  2: pendiente recepcion
     *  3: cerrada con éxito
     */
    private int estado;
}
