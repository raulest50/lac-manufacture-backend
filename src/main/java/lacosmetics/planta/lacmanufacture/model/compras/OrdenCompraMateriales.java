package lacosmetics.planta.lacmanufacture.model.compras;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.commons.divisas.Divisas.DIVISAS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraMateriales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_compra_id", unique = true, updatable = false, nullable = false)
    private int ordenCompraId;

    @CreationTimestamp
    private LocalDateTime fechaEmision;

    private LocalDateTime fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "proveedor_uuid", referencedColumnName = "uuid")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "ordenCompraMateriales", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemOrdenCompra> itemsOrdenCompra;

    private int subTotal;
    private int ivaCOP;
    private int totalPagar;

    /**
     * 0: credito
     * 1: contado
     */
    private String condicionPago;

    private String tiempoEntrega;

    private int plazoPago;

    /**
     * -1: cancelada
     *  0: pendiente liberacion
     *  1: pendiente envio
     *  2: pendiente ingreso almacen
     *  3: cerrada con éxito
     */
    private int estado;

    /**
     * Plain column to store the FacturaCompra ID supplied by the provider.
     */
    private Integer facturaCompraId;

    private DIVISAS divisas;

    private double trm;

    @Column(name = "observaciones")
    private String observaciones;

}
