package lacosmetics.planta.lacmanufacture.model.activos.fijos.compras;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.commons.divisas.Divisas.DIVISAS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "proveedor_uuid", referencedColumnName = "uuid")
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
     * -1: cancelada
     *  0: pendiente liberacion
     *  1: pendiente envio
     *  2: pendiente recepcion
     *  3: cerrada con éxito
     */
    private int estado;

    /**
     * para soportar ordenes de compra en dolares.
     */
    private DIVISAS divisa;

    private double trm;

    /**
     * Referencia a la factura de compra asociada a esta orden
     */
    private Integer facturaCompraActivoId;

    /**
     * Lista de ítems de la orden de compra
     */
    @OneToMany(mappedBy = "ordenCompraActivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemOrdenCompraActivo> itemsOrdenCompra = new ArrayList<>();


}
